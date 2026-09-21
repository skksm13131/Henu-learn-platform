package com.hwz.common.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

/**
 * 批量下载附件的短时下载票据。
 *
 * <p>管理员在浏览器里原生导航到下载链接时无法携带 Authorization 头，因此先通过普通接口
 * 换取一个几分钟过期、只对指定考核 + 下载任务有效的签名票据，再把它拼进下载链接的 query 里。
 */
@Component
public class DownloadTicketService {

    private static final String PREFIX = "dt-";
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final byte[] secret;
    private final long ttlSeconds;

    public DownloadTicketService(
            @Value("${labcore.auth.access-token-secret:change-this-local-dev-token-secret}") String secret,
            @Value("${labcore.auth.download-ticket-ttl-seconds:300}") long ttlSeconds) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.ttlSeconds = ttlSeconds;
    }

    public long getTtlSeconds() {
        return ttlSeconds;
    }

    public String issue(Long adminUserId, Long assignmentId, String downloadId) {
        if (adminUserId == null || adminUserId <= 0 || assignmentId == null || assignmentId <= 0
                || !StringUtils.hasText(downloadId)) {
            throw new IllegalArgumentException("下载票据签发参数不正确");
        }
        long expiresAt = Instant.now().getEpochSecond() + ttlSeconds;
        String payload = adminUserId + ":" + assignmentId + ":" + downloadId + ":" + expiresAt + ":" + UUID.randomUUID();
        String encodedPayload = base64Url(payload.getBytes(StandardCharsets.UTF_8));
        return PREFIX + encodedPayload + "." + sign(encodedPayload);
    }

    public Long verifyAndGetUserId(String rawTicket) {
        return verify(rawTicket).getAdminUserId();
    }

    public DownloadTicket verifyScoped(String rawTicket, Long assignmentId, String downloadId) {
        DownloadTicket ticket = verify(rawTicket);
        if (assignmentId == null || assignmentId != ticket.getAssignmentId()) {
            throw unauthorized();
        }
        if (!StringUtils.hasText(downloadId) || !downloadId.equals(ticket.getDownloadId())) {
            throw unauthorized();
        }
        return ticket;
    }

    private DownloadTicket verify(String rawTicket) {
        if (!StringUtils.hasText(rawTicket) || !rawTicket.startsWith(PREFIX)) {
            throw unauthorized();
        }

        String body = rawTicket.substring(PREFIX.length());
        int dot = body.indexOf('.');
        if (dot <= 0 || dot == body.length() - 1) {
            throw unauthorized();
        }

        String encodedPayload = body.substring(0, dot);
        String signature = body.substring(dot + 1);
        if (!constantTimeEquals(signature, sign(encodedPayload))) {
            throw unauthorized();
        }

        String payload;
        try {
            payload = new String(Base64.getUrlDecoder().decode(encodedPayload), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ex) {
            throw unauthorized();
        }

        String[] parts = payload.split(":", 5);
        if (parts.length != 5) {
            throw unauthorized();
        }

        try {
            long adminUserId = Long.parseLong(parts[0]);
            long assignmentId = Long.parseLong(parts[1]);
            String downloadId = parts[2];
            long expiresAt = Long.parseLong(parts[3]);
            // parts[4] 为随机 nonce，仅用于保证票据不可预测，无需解析校验
            if (adminUserId <= 0 || assignmentId <= 0 || !StringUtils.hasText(downloadId)
                    || expiresAt < Instant.now().getEpochSecond()) {
                throw unauthorized();
            }
            return new DownloadTicket(adminUserId, assignmentId, downloadId, expiresAt);
        } catch (NumberFormatException ex) {
            throw unauthorized();
        }
    }

    private String sign(String payload) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
            return base64Url(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("下载票据签发失败", ex);
        }
    }

    private String base64Url(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }

    private boolean constantTimeEquals(String left, String right) {
        byte[] a = left == null ? new byte[0] : left.getBytes(StandardCharsets.UTF_8);
        byte[] b = right == null ? new byte[0] : right.getBytes(StandardCharsets.UTF_8);
        int diff = a.length ^ b.length;
        for (int i = 0; i < Math.min(a.length, b.length); i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }

    private ResponseStatusException unauthorized() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "下载链接无效或已过期");
    }

    public static final class DownloadTicket {
        private final long adminUserId;
        private final long assignmentId;
        private final String downloadId;
        private final long expiresAt;

        public DownloadTicket(long adminUserId, long assignmentId, String downloadId, long expiresAt) {
            this.adminUserId = adminUserId;
            this.assignmentId = assignmentId;
            this.downloadId = downloadId;
            this.expiresAt = expiresAt;
        }

        public long getAdminUserId() {
            return adminUserId;
        }

        public long getAssignmentId() {
            return assignmentId;
        }

        public String getDownloadId() {
            return downloadId;
        }

        public long getExpiresAt() {
            return expiresAt;
        }
    }
}
