package com.ehrs.backend.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthTokenService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    @Value("${ehrs.auth.token-secret:EHRS_LOCAL_DEV_SECRET_CHANGE_ME}")
    private String tokenSecret;

    @Value("${ehrs.auth.token-hours:12}")
    private long tokenHours;

    public String createToken(UUID userId) {
        long expiresAtEpochSeconds = Instant.now().plusSeconds(tokenHours * 3600).getEpochSecond();
        String payload = userId + "." + expiresAtEpochSeconds;
        String signature = sign(payload);
        String tokenValue = payload + "." + signature;

        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(tokenValue.getBytes(StandardCharsets.UTF_8));
    }

    public Optional<UUID> parseUserId(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        try {
            String tokenValue = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            String[] parts = tokenValue.split("\\.");
            if (parts.length != 3) {
                return Optional.empty();
            }

            String payload = parts[0] + "." + parts[1];
            if (!sign(payload).equals(parts[2])) {
                return Optional.empty();
            }

            long expiresAtEpochSeconds = Long.parseLong(parts[1]);
            if (Instant.now().getEpochSecond() >= expiresAtEpochSeconds) {
                return Optional.empty();
            }

            return Optional.of(UUID.fromString(parts[0]));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(tokenSecret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
            mac.init(secretKey);
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to sign authentication token", ex);
        }
    }
}
