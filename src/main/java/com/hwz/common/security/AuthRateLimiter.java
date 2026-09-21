package com.hwz.common.security;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthRateLimiter {

    private static final int LOGIN_MAX_FAILURES = 5;
    private static final Duration LOGIN_WINDOW = Duration.ofMinutes(1);
    private static final Duration LOGIN_LOCK = Duration.ofMinutes(30);
    private static final int LOGIN_MAX_ATTEMPTS = 5;
    private static final Duration LOGIN_ATTEMPT_WINDOW = Duration.ofMinutes(1);

    private static final int REGISTER_MAX_ATTEMPTS = 5;
    private static final Duration REGISTER_WINDOW = Duration.ofMinutes(1);

    private static final int REFRESH_MAX_ATTEMPTS = 20;
    private static final Duration REFRESH_WINDOW = Duration.ofMinutes(1);
    private static final int MAX_TRACKED_KEYS = 10000;

    private final Map<String, AttemptState> states = new ConcurrentHashMap<>();

    public void checkLoginAllowed(String username, HttpServletRequest request) {
        String ip = clientIp(request);
        String normalizedUsername = normalize(username);
        assertAllowed("login:ip:" + ip, "登录尝试过于频繁，请稍后再试");
        assertAllowed("login:user:" + normalizedUsername, "登录尝试过于频繁，请稍后再试");
        checkWindowAllowed("login_req:ip:" + ip,
                LOGIN_MAX_ATTEMPTS,
                LOGIN_ATTEMPT_WINDOW,
                "登录尝试过于频繁，请稍后再试");
        checkWindowAllowed("login_req:user:" + normalizedUsername,
                LOGIN_MAX_ATTEMPTS,
                LOGIN_ATTEMPT_WINDOW,
                "登录尝试过于频繁，请稍后再试");
    }

    public void recordLoginFailure(String username, HttpServletRequest request) {
        Instant now = Instant.now();
        recordFailure("login:ip:" + clientIp(request), now, LOGIN_MAX_FAILURES, LOGIN_WINDOW, LOGIN_LOCK);
        recordFailure("login:user:" + normalize(username), now, LOGIN_MAX_FAILURES, LOGIN_WINDOW, LOGIN_LOCK);
    }

    public void recordLoginSuccess(String username, HttpServletRequest request) {
        states.remove("login:user:" + normalize(username));
    }

    public void checkRegisterAllowed(HttpServletRequest request) {
        checkWindowAllowed("register:ip:" + clientIp(request),
                REGISTER_MAX_ATTEMPTS,
                REGISTER_WINDOW,
                "注册尝试过于频繁，请稍后再试");
    }

    public void checkRefreshAllowed(HttpServletRequest request) {
        checkWindowAllowed("refresh:ip:" + clientIp(request),
                REFRESH_MAX_ATTEMPTS,
                REFRESH_WINDOW,
                "登录状态刷新过于频繁，请稍后再试");
    }

    private void checkWindowAllowed(String key, int maxAttempts, Duration window, String message) {
        Instant now = Instant.now();
        pruneIfNeeded(now);
        AttemptState state = states.computeIfAbsent(key, ignored -> new AttemptState());
        synchronized (state) {
            if (state.windowStartedAt == null || state.windowStartedAt.plus(window).isBefore(now)) {
                state.windowStartedAt = now;
                state.failures = 0;
            }
            state.failures++;
            if (state.failures > maxAttempts) {
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, message);
            }
        }
    }

    private void assertAllowed(String key, String message) {
        AttemptState state = states.get(key);
        if (state != null && state.lockedUntil != null && state.lockedUntil.isAfter(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, message);
        }
    }

    private void recordFailure(String key, Instant now, int maxFailures, Duration window, Duration lock) {
        pruneIfNeeded(now);
        AttemptState state = states.computeIfAbsent(key, ignored -> new AttemptState());
        synchronized (state) {
            if (state.windowStartedAt == null || state.windowStartedAt.plus(window).isBefore(now)) {
                state.windowStartedAt = now;
                state.failures = 0;
            }
            state.failures++;
            if (state.failures >= maxFailures) {
                state.lockedUntil = now.plus(lock);
            }
        }
    }

    private String normalize(String username) {
        return username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
    }

    private void pruneIfNeeded(Instant now) {
        if (states.size() < MAX_TRACKED_KEYS) {
            return;
        }
        states.entrySet().removeIf(entry -> {
            AttemptState state = entry.getValue();
            synchronized (state) {
                boolean unlocked = state.lockedUntil == null || state.lockedUntil.isBefore(now);
                boolean staleWindow = state.windowStartedAt == null
                        || state.windowStartedAt.plus(LOGIN_LOCK).isBefore(now);
                return unlocked && staleWindow;
            }
        });
    }

    private String clientIp(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String remoteAddr = request.getRemoteAddr();
        String forwarded = request.getHeader("X-Forwarded-For");
        if (isLoopback(remoteAddr) && forwarded != null && !forwarded.trim().isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return remoteAddr == null ? "unknown" : remoteAddr;
    }

    private boolean isLoopback(String remoteAddr) {
        return "127.0.0.1".equals(remoteAddr)
                || "0:0:0:0:0:0:0:1".equals(remoteAddr)
                || "::1".equals(remoteAddr);
    }

    private static class AttemptState {
        private Instant windowStartedAt;
        private int failures;
        private Instant lockedUntil;
    }
}
