package com.hwz.common.util;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public final class PasswordSupport {

    private static final Pattern BCRYPT_PATTERN = Pattern.compile("^\\$2[aby]?\\$\\d{2}\\$.{53}$");

    private PasswordSupport() {
    }

    public static boolean isEncoded(String value) {
        return StringUtils.hasText(value) && BCRYPT_PATTERN.matcher(value).matches();
    }

    public static boolean matches(PasswordEncoder passwordEncoder, String rawPassword, String storedPassword) {
        if (!StringUtils.hasText(rawPassword) || !StringUtils.hasText(storedPassword)) {
            return false;
        }
        if (isEncoded(storedPassword)) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }
        return rawPassword.equals(storedPassword);
    }

    public static String encodeIfNeeded(PasswordEncoder passwordEncoder, String rawOrEncodedPassword) {
        if (!StringUtils.hasText(rawOrEncodedPassword)) {
            return rawOrEncodedPassword;
        }
        return isEncoded(rawOrEncodedPassword)
                ? rawOrEncodedPassword
                : passwordEncoder.encode(rawOrEncodedPassword);
    }
}
