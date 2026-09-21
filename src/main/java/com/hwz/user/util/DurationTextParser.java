package com.hwz.user.util;

import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.OptionalLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DurationTextParser {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)");

    private DurationTextParser() {
    }

    public static OptionalLong parseSeconds(String text) {
        if (!StringUtils.hasText(text)) {
            return OptionalLong.empty();
        }

        String normalized = text.trim().toLowerCase();
        Matcher matcher = NUMBER_PATTERN.matcher(normalized);
        if (!matcher.find()) {
            return OptionalLong.empty();
        }

        BigDecimal value = new BigDecimal(matcher.group(1));
        BigDecimal seconds;

        if (normalized.contains("hour") || normalized.contains("hours") || normalized.contains("小时") || normalized.contains("h")) {
            seconds = value.multiply(BigDecimal.valueOf(3600));
        } else if (normalized.contains("minute") || normalized.contains("minutes") || normalized.contains("分钟") || normalized.contains("分")) {
            seconds = value.multiply(BigDecimal.valueOf(60));
        } else if (normalized.contains("day") || normalized.contains("days") || normalized.contains("天")) {
            seconds = value.multiply(BigDecimal.valueOf(86400));
        } else {
            seconds = value;
        }

        return OptionalLong.of(seconds.setScale(0, RoundingMode.HALF_UP).longValue());
    }
}
