package com.hwz.user.service;

import com.hwz.user.dto.LoginRequest;
import com.hwz.user.dto.LoginResponse;
import com.hwz.user.dto.RefreshTokenRequest;
import com.hwz.user.dto.RegisterRequest;
import com.hwz.user.dto.TokenResponse;
import com.hwz.user.dto.UserInfoResponse;

public interface AuthService {
    LoginResponse login(LoginRequest req);

    LoginResponse register(RegisterRequest req);

    TokenResponse refreshToken(RefreshTokenRequest req);

    UserInfoResponse getCurrentUser(String authorization);
}
