package com.seal.seal.service;

import com.seal.seal.entity.RefreshToken;
import com.seal.seal.entity.User;
import com.seal.seal.repository.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenService.class);

    @Value("${jwt.refresh.expiration:604800000}") // 7 days default
    private Long refreshTokenExpiration;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public RefreshToken createRefreshToken(User user) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000);
        
        RefreshToken refreshToken = new RefreshToken(token, user, expiresAt);
        RefreshToken saved = refreshTokenRepository.save(refreshToken);
        
        logger.info("Created refresh token for user: {}", user.getPhone());
        return saved;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired. Please login again");
        }
        return token;
    }

    @Transactional
    public void revokeUserTokens(Long userId) {
        logger.info("Revoking all refresh tokens for user: {}", userId);
        refreshTokenRepository.revokeAllUserTokens(userId);
    }

    @Transactional
    public void deleteToken(RefreshToken token) {
        refreshTokenRepository.delete(token);
        logger.debug("Deleted refresh token: {}", token.getId());
    }
}
