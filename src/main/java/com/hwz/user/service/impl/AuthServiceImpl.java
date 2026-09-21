package com.hwz.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hwz.common.auth.AccessTokenService;
import com.hwz.common.entity.RefreshToken;
import com.hwz.common.entity.User;
import com.hwz.common.mapper.RefreshTokenMapper;
import com.hwz.common.mapper.UserMapper;
import com.hwz.common.util.PasswordSupport;
import com.hwz.user.dto.LoginRequest;
import com.hwz.user.dto.LoginResponse;
import com.hwz.user.dto.RefreshTokenRequest;
import com.hwz.user.dto.RegisterRequest;
import com.hwz.user.dto.TokenResponse;
import com.hwz.user.dto.UserInfoResponse;
import com.hwz.user.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private static final String REFRESH_PREFIX = "refresh-";
    private static final int REFRESH_DAYS = 7;
    private static final int MIN_PASSWORD_LENGTH = 12;

    private final UserMapper userMapper;
    private final RefreshTokenMapper refreshTokenMapper;
    private final PasswordEncoder passwordEncoder;
    private final AccessTokenService accessTokenService;

    public AuthServiceImpl(UserMapper userMapper,
                           RefreshTokenMapper refreshTokenMapper,
                           PasswordEncoder passwordEncoder,
                           AccessTokenService accessTokenService) {
        this.userMapper = userMapper;
        this.refreshTokenMapper = refreshTokenMapper;
        this.passwordEncoder = passwordEncoder;
        this.accessTokenService = accessTokenService;
    }

    @Override
    public LoginResponse login(LoginRequest req) {
        if (req == null || !StringUtils.hasText(req.getUsername()) || !StringUtils.hasText(req.getPassword())) {
            throw new IllegalArgumentException("请输入用户名和密码");
        }

        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, req.getUsername())
                        .last("LIMIT 1")
        );

        if (user == null || !PasswordSupport.matches(passwordEncoder, req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        LocalDateTime now = LocalDateTime.now();
        if (!PasswordSupport.isEncoded(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        user.setLastLoginTime(now);
        user.setUpdatedTime(now);
        userMapper.updateById(user);

        String accessToken = accessTokenService.issue(user.getId());
        String refreshToken = issueRefreshToken(user.getId());
        return LoginResponse.from(user, accessToken, refreshToken);
    }

    @Override
    public LoginResponse register(RegisterRequest req) {
        if (req == null || !StringUtils.hasText(req.getUsername()) || !StringUtils.hasText(req.getPassword())) {
            throw new IllegalArgumentException("请输入用户名和密码");
        }
        validatePasswordStrength(req.getPassword());
        if (StringUtils.hasText(req.getConfirmPassword()) && !req.getPassword().equals(req.getConfirmPassword())) {
            throw new IllegalArgumentException("两次输入的密码不一致");
        }

        User existing = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, req.getUsername())
                        .last("LIMIT 1")
        );
        if (existing != null) {
            throw new IllegalArgumentException("用户名已存在，请更换后重试");
        }

        String displayName = StringUtils.hasText(req.getRealName()) ? req.getRealName() : req.getUsername();
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .displayName(displayName)
                .email(req.getEmail())
                .realName(req.getRealName())
                .grade(req.getGrade())
                .role(User.Role.USER)
                .status(User.Status.ACTIVE)
                .createdTime(now)
                .updatedTime(now)
                .build();
        userMapper.insert(user);

        String accessToken = accessTokenService.issue(user.getId());
        String refreshToken = issueRefreshToken(user.getId());
        return LoginResponse.from(user, accessToken, refreshToken);
    }

    @Override
    public TokenResponse refreshToken(RefreshTokenRequest req) {
        if (req == null || !StringUtils.hasText(req.getRefreshToken())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "登录状态已失效，请重新登录");
        }

        RefreshToken token = refreshTokenMapper.selectOne(
                new LambdaQueryWrapper<RefreshToken>()
                        .eq(RefreshToken::getToken, req.getRefreshToken())
                        .isNull(RefreshToken::getRevokedAt)
                        .last("LIMIT 1")
        );

        LocalDateTime now = LocalDateTime.now();
        if (token == null || token.getExpiresAt() == null || token.getExpiresAt().isBefore(now)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "登录状态已过期，请重新登录");
        }
        if (token.getCreatedAt() != null && token.getCreatedAt().plusDays(REFRESH_DAYS).isBefore(now)) {
            token.setRevokedAt(now);
            refreshTokenMapper.updateById(token);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "登录状态已过期，请重新登录");
        }

        String accessToken = accessTokenService.issue(token.getUserId());
        String newRefreshToken = REFRESH_PREFIX + UUID.randomUUID();
        token.setToken(newRefreshToken);
        LocalDateTime absoluteExpiresAt = token.getCreatedAt() == null ? now.plusDays(REFRESH_DAYS) : token.getCreatedAt().plusDays(REFRESH_DAYS);
        LocalDateTime rollingExpiresAt = now.plusDays(REFRESH_DAYS);
        token.setExpiresAt(rollingExpiresAt.isBefore(absoluteExpiresAt) ? rollingExpiresAt : absoluteExpiresAt);
        refreshTokenMapper.updateById(token);

        return TokenResponse.builder()
                .token(accessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Override
    public UserInfoResponse getCurrentUser(String authorization) {
        Long userId = accessTokenService.verifyAndGetUserId(authorization);
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户不存在，请重新登录");
        }
        return UserInfoResponse.from(user);
    }

    private String issueRefreshToken(Long userId) {
        String refreshToken = REFRESH_PREFIX + UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        RefreshToken record = RefreshToken.builder()
                .userId(userId)
                .token(refreshToken)
                .createdAt(now)
                .expiresAt(now.plusDays(REFRESH_DAYS))
                .build();
        refreshTokenMapper.insert(record);
        return refreshToken;
    }

    private void validatePasswordStrength(String password) {
        if (!isStrongPassword(password)) {
            if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
                throw new IllegalArgumentException("密码长度至少需要 12 位");
            }
            throw new IllegalArgumentException("密码需包含大写字母、小写字母、数字和特殊字符");
        }
    }

    private boolean isStrongPassword(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            return false;
        }
        boolean hasLower = false;
        boolean hasUpper = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        for (int i = 0; i < password.length(); i++) {
            char ch = password.charAt(i);
            if (Character.isLowerCase(ch)) {
                hasLower = true;
            } else if (Character.isUpperCase(ch)) {
                hasUpper = true;
            }
            if (Character.isDigit(ch)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(ch)) {
                hasSpecial = true;
            }
        }
        return hasLower && hasUpper && hasDigit && hasSpecial;
    }

}
