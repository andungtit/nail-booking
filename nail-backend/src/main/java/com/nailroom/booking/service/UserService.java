package com.nailroom.booking.service;

import com.nailroom.booking.dto.LoginRequest;
import com.nailroom.booking.entity.Role;
import com.nailroom.booking.entity.User;
import com.nailroom.booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final String adminUsername;
    private final String adminPassword;
    private final byte[] tokenSecret;
    private final long tokenTtlSeconds;

    public UserService(UserRepository userRepository,
                       @Value("${app.admin.username:admin}") String adminUsername,
                       @Value("${app.admin.password:admin123}") String adminPassword,
                       @Value("${app.admin.token-secret:change-me-please-nailroom-secret-key}") String tokenSecret,
                       @Value("${app.admin.token-ttl-hours:12}") long tokenTtlHours) {
        this.userRepository = userRepository;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.tokenSecret = tokenSecret.getBytes(StandardCharsets.UTF_8);
        this.tokenTtlSeconds = tokenTtlHours * 3600;
    }

    public Map<String, Object> login(LoginRequest request) {
        String u = request.getUsername() != null ? request.getUsername().trim() : "";
        String p = request.getPassword() != null ? request.getPassword() : "";

        boolean matchesAdmin = MessageDigest.isEqual(adminUsername.getBytes(StandardCharsets.UTF_8), u.getBytes(StandardCharsets.UTF_8))
                && MessageDigest.isEqual(adminPassword.getBytes(StandardCharsets.UTF_8), p.getBytes(StandardCharsets.UTF_8));

        if (!matchesAdmin) {
            User user = userRepository.findByUsername(u)
                    .orElseThrow(() -> new IllegalArgumentException("Sai tài khoản hoặc mật khẩu."));
            if (!user.getPassword().equals(p)) {
                throw new IllegalArgumentException("Sai tài khoản hoặc mật khẩu.");
            }
        }

        Instant exp = Instant.now().plusSeconds(tokenTtlSeconds);
        String payload = b64(u + "|" + exp.getEpochSecond());
        String token = payload + "." + sign(payload);

        return Map.of(
                "token", token,
                "username", u,
                "expiresAt", exp.toString()
        );
    }

    public String verifyToken(String token) {
        if (token == null) return null;
        int dot = token.indexOf('.');
        if (dot <= 0) return null;
        String payload = token.substring(0, dot);
        String sig = token.substring(dot + 1);
        if (!MessageDigest.isEqual(sign(payload).getBytes(StandardCharsets.UTF_8), sig.getBytes(StandardCharsets.UTF_8))) {
            return null;
        }
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);
            int bar = decoded.lastIndexOf('|');
            long exp = Long.parseLong(decoded.substring(bar + 1));
            if (Instant.now().getEpochSecond() > exp) return null;
            return decoded.substring(0, bar);
        } catch (Exception e) {
            return null;
        }
    }

    private String sign(String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(tokenSecret, "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static String b64(String s) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }
}
