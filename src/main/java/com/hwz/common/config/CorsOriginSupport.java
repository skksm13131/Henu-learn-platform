package com.hwz.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class CorsOriginSupport {

    private final String[] allowedOrigins;
    private final Set<String> allowedOriginSet;

    public CorsOriginSupport(@Value("${labcore.cors.allowed-origins:http://localhost:5173,http://127.0.0.1:5173,http://localhost:8080,http://127.0.0.1:8080}") String origins) {
        this.allowedOriginSet = new LinkedHashSet<>();
        Arrays.stream(origins.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .forEach(allowedOriginSet::add);
        this.allowedOrigins = allowedOriginSet.toArray(new String[0]);
    }

    public String[] allowedOrigins() {
        return allowedOrigins.clone();
    }

    public boolean contains(String origin) {
        return allowedOriginSet.contains(origin);
    }
}
