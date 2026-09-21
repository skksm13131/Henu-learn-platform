package com.hwz.user.controller;

import com.hwz.user.dto.LoginRequest;
import com.hwz.user.dto.LoginResponse;
import com.hwz.user.dto.RefreshTokenRequest;
import com.hwz.user.dto.RegisterRequest;
import com.hwz.common.Result;
import com.hwz.common.security.AuthRateLimiter;
import com.hwz.common.security.CaptchaService;
import com.hwz.user.dto.CaptchaResponse;
import com.hwz.user.dto.TokenResponse;
import com.hwz.user.dto.UserInfoResponse;
import com.hwz.user.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthRateLimiter authRateLimiter;
    private final CaptchaService captchaService;
    private final boolean registrationEnabled;

    public AuthController(AuthService authService,
                          AuthRateLimiter authRateLimiter,
                          CaptchaService captchaService,
                          @Value("${labcore.auth.registration-enabled:false}") boolean registrationEnabled) {
        this.authService = authService;
        this.authRateLimiter = authRateLimiter;
        this.captchaService = captchaService;
        this.registrationEnabled = registrationEnabled;
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest req, HttpServletRequest request) {
        String username = req == null ? null : req.getUsername();
        authRateLimiter.checkLoginAllowed(username, request);
        captchaService.verify(req == null ? null : req.getCaptchaId(), req == null ? null : req.getCaptchaCode());
        try {
            LoginResponse response = authService.login(req);
            authRateLimiter.recordLoginSuccess(username, request);
            return Result.ok(response);
        } catch (IllegalArgumentException ex) {
            authRateLimiter.recordLoginFailure(username, request);
            throw ex;
        }
    }

    @PostMapping("/register")
    public Result<LoginResponse> register(@RequestBody RegisterRequest req, HttpServletRequest request) {
        if (!registrationEnabled) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "注册功能已关闭，请联系管理员创建账号");
        }
        authRateLimiter.checkRegisterAllowed(request);
        captchaService.verify(req == null ? null : req.getCaptchaId(), req == null ? null : req.getCaptchaCode());
        return Result.ok(authService.register(req));
    }

    @GetMapping("/captcha")
    public Result<CaptchaResponse> captcha() {
        return Result.ok(captchaService.issue());
    }

    @PostMapping("/refresh")
    public Result<TokenResponse> refresh(@RequestBody RefreshTokenRequest req, HttpServletRequest request) {
        authRateLimiter.checkRefreshAllowed(request);
        return Result.ok(authService.refreshToken(req));
    }

    @GetMapping("/me")
    public Result<UserInfoResponse> me(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return Result.ok(authService.getCurrentUser(authorization));
    }
}
