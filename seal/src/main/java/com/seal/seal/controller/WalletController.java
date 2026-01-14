package com.seal.seal.controller;

import com.seal.seal.dto.WalletResponse;
import com.seal.seal.entity.Wallet;
import com.seal.seal.service.UserService;
import com.seal.seal.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private static final Logger logger = LoggerFactory.getLogger(WalletController.class);

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;

    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(Authentication authentication) {
        try {
            String phone = authentication.getName();
            logger.info("Balance inquiry for user: {}", phone);
            
            Wallet wallet = walletService.findByUserPhone(phone);
            
            WalletResponse response = new WalletResponse(
                wallet.getId(),
                wallet.getBalance(),
                wallet.getStatus(),
                wallet.getUser().getPhone()
            );
            
            logger.debug("Balance retrieved for user {}: {}", phone, wallet.getBalance());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error retrieving balance for user {}: {}", authentication.getName(), e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
