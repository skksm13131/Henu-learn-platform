package com.hwz.common.security;

import com.hwz.user.dto.CaptchaResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CaptchaService {

    private static final int WIDTH = 120;
    private static final int HEIGHT = 42;
    private static final Duration TTL = Duration.ofMinutes(5);
    private static final int MAX_CAPTCHAS = 5000;
    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final SecureRandom random = new SecureRandom();
    private final Map<String, CaptchaEntry> captchas = new ConcurrentHashMap<>();

    public CaptchaResponse issue() {
        Instant now = Instant.now();
        prune(now);

        String code = randomCode();
        String captchaId = UUID.randomUUID().toString();
        captchas.put(captchaId, new CaptchaEntry(code, now.plus(TTL)));
        return new CaptchaResponse(captchaId, renderImage(code));
    }

    public void verify(String captchaId, String captchaCode) {
        if (!StringUtils.hasText(captchaId) || !StringUtils.hasText(captchaCode)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "请输入验证码");
        }
        CaptchaEntry entry = captchas.remove(captchaId);
        if (entry == null || entry.expiresAt.isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "验证码已过期，请刷新后重试");
        }
        String submitted = captchaCode.trim().toUpperCase(Locale.ROOT);
        if (!entry.code.equals(submitted)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "验证码错误，请重新输入");
        }
    }

    private String randomCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            code.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return code.toString();
    }

    private String renderImage(String code) {
        try {
            BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setColor(new Color(248, 250, 252));
            graphics.fillRect(0, 0, WIDTH, HEIGHT);

            for (int i = 0; i < 12; i++) {
                graphics.setColor(new Color(120 + random.nextInt(100), 120 + random.nextInt(100), 120 + random.nextInt(100)));
                int x1 = random.nextInt(WIDTH);
                int y1 = random.nextInt(HEIGHT);
                int x2 = random.nextInt(WIDTH);
                int y2 = random.nextInt(HEIGHT);
                graphics.drawLine(x1, y1, x2, y2);
            }

            graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
            for (int i = 0; i < code.length(); i++) {
                graphics.setColor(new Color(30 + random.nextInt(60), 60 + random.nextInt(80), 110 + random.nextInt(70)));
                graphics.drawString(String.valueOf(code.charAt(i)), 15 + i * 25, 30 + random.nextInt(5));
            }
            graphics.dispose();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "验证码生成失败，请稍后重试");
        }
    }

    private void prune(Instant now) {
        if (captchas.size() < MAX_CAPTCHAS) {
            captchas.entrySet().removeIf(entry -> entry.getValue().expiresAt.isBefore(now));
            return;
        }
        captchas.entrySet().removeIf(entry -> entry.getValue().expiresAt.isBefore(now));
    }

    private static class CaptchaEntry {
        private final String code;
        private final Instant expiresAt;

        private CaptchaEntry(String code, Instant expiresAt) {
            this.code = code;
            this.expiresAt = expiresAt;
        }
    }
}
