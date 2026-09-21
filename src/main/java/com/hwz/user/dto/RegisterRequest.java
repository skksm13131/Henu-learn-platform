package com.hwz.user.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
    private String realName;
    private String grade;
    private String captchaId;
    private String captchaCode;
}
