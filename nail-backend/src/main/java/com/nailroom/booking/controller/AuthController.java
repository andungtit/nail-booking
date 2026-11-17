package com.nailroom.booking.controller;

import com.nailroom.booking.dto.LoginRequest;
import com.nailroom.booking.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping({"/api/auth", "/api/users"})
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @GetMapping("/me")
    public Map<String, Object> me(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = (authHeader != null && authHeader.startsWith("Bearer "))
                ? authHeader.substring(7).trim() : null;

        String user = userService.verifyToken(token);
        if (user == null) {
            throw new IllegalArgumentException("Phiên đăng nhập đã hết hạn.");
        }

        return Map.of(
                "username", user,
                "serverTime", Instant.now().toString()
        );
    }
}
