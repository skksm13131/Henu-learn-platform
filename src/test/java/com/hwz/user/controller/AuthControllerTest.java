package com.hwz.user.controller;

import com.hwz.common.security.AuthRateLimiter;
import com.hwz.common.security.CaptchaService;
import com.hwz.user.dto.RegisterRequest;
import com.hwz.user.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class AuthControllerTest {

    @Test
    void registerIsBlockedBeforeCaptchaOrUserCreationWhenDisabled() {
        AuthService authService = mock(AuthService.class);
        AuthRateLimiter authRateLimiter = mock(AuthRateLimiter.class);
        CaptchaService captchaService = mock(CaptchaService.class);
        AuthController controller = new AuthController(authService, authRateLimiter, captchaService, false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.register(new RegisterRequest(), new MockHttpServletRequest())
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("注册功能已关闭，请联系管理员创建账号", exception.getReason());
        verifyNoInteractions(authService, authRateLimiter, captchaService);
    }
}
