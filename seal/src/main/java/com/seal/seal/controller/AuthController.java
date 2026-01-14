package com.seal.seal.controller;

import com.seal.seal.dto.AuthResponse;
import com.seal.seal.dto.LoginRequest;
import com.seal.seal.dto.RefreshTokenRequest;
import com.seal.seal.dto.RegisterRequest;
import com.seal.seal.entity.RefreshToken;
import com.seal.seal.entity.User;
import com.seal.seal.service.RefreshTokenService;
import com.seal.seal.service.UserService;
import com.seal.seal.util.JwtUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            logger.info("Registration attempt for phone: {}", request.getPhone());
            
            User user = userService.registerUser(request.getPhone(), request.getPassword());
            String accessToken = jwtUtil.generateToken(user.getPhone());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
            
            logger.info("User registered successfully: {}", user.getPhone());
            AuthResponse response = new AuthResponse(accessToken, refreshToken.getToken(), 
                                                   user.getPhone(), "User registered successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Registration failed for phone {}: {}", request.getPhone(), e.getMessage());
            return ResponseEntity.badRequest().body(new AuthResponse(null, null, e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            logger.info("Login attempt for phone: {}", request.getPhone());
            
            User user = userService.findByPhone(request.getPhone());
            
            if (!userService.validatePassword(request.getPassword(), user.getPassword())) {
                logger.warn("Invalid password for phone: {}", request.getPhone());
                return ResponseEntity.badRequest().body(new AuthResponse(null, null, "Invalid credentials"));
            }

            if (!"ACTIVE".equals(user.getStatus())) {
                logger.warn("Login attempt for blocked account: {}", request.getPhone());
                return ResponseEntity.badRequest().body(new AuthResponse(null, null, "Account is blocked"));
            }

            userService.updateLastLogin(request.getPhone());
            String accessToken = jwtUtil.generateToken(user.getPhone());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
            
            logger.info("Login successful for phone: {}", request.getPhone());
            AuthResponse response = new AuthResponse(accessToken, refreshToken.getToken(), 
                                                   user.getPhone(), "Login successful");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Login failed for phone {}: {}", request.getPhone(), e.getMessage());
            return ResponseEntity.badRequest().body(new AuthResponse(null, null, "Invalid credentials"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            logger.info("Token refresh attempt");
            
            RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken())
                    .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
            
            refreshToken = refreshTokenService.verifyExpiration(refreshToken);
            
            if (refreshToken.isRevoked()) {
                throw new RuntimeException("Refresh token has been revoked");
            }
            
            User user = refreshToken.getUser();
            String newAccessToken = jwtUtil.generateToken(user.getPhone());
            
            // Rotate refresh token
            refreshTokenService.deleteToken(refreshToken);
            RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
            
            logger.info("Token refreshed successfully for user: {}", user.getPhone());
            AuthResponse response = new AuthResponse(newAccessToken, newRefreshToken.getToken(), 
                                                   user.getPhone(), "Token refreshed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Token refresh failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new AuthResponse(null, null, e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication) {
        try {
            String phone = authentication.getName();
            User user = userService.findByPhone(phone);
            
            logger.info("Logout for user: {}", phone);
            refreshTokenService.revokeUserTokens(user.getId());
            
            return ResponseEntity.ok().body(new AuthResponse(null, null, "Logged out successfully"));
        } catch (Exception e) {
            logger.error("Logout failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new AuthResponse(null, null, "Logout failed"));
        }
    }
}
