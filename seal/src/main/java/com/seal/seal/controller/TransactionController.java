package com.seal.seal.controller;

import com.seal.seal.dto.TransactionResponse;
import com.seal.seal.dto.TransferRequest;
import com.seal.seal.entity.Transaction;
import com.seal.seal.entity.User;
import com.seal.seal.service.TransactionService;
import com.seal.seal.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @PostMapping("/transfer")
    public ResponseEntity<?> transferMoney(@Valid @RequestBody TransferRequest request, 
                                         Authentication authentication) {
        try {
            String fromPhone = authentication.getName();
            
            logger.info("Transfer request: from={}, to={}, amount={}", fromPhone, request.getToPhone(), request.getAmount());
            
            if (fromPhone.equals(request.getToPhone())) {
                logger.warn("Self-transfer attempt by user: {}", fromPhone);
                return ResponseEntity.badRequest().body("Cannot transfer to yourself");
            }

            Transaction transaction = transactionService.transferMoney(
                fromPhone, request.getToPhone(), request.getAmount());
            
            TransactionResponse response = new TransactionResponse(
                transaction.getId(),
                transaction.getFromWallet().getUser().getPhone(),
                transaction.getToWallet().getUser().getPhone(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getCreatedAt()
            );
            
            logger.info("Transfer successful: transactionId={}, amount={}", transaction.getId(), request.getAmount());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Transfer failed for user {}: {}", authentication.getName(), e.getMessage(), e);
            return ResponseEntity.badRequest().body("Transfer failed: " + e.getMessage());
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getTransactionHistory(Authentication authentication) {
        try {
            String phone = authentication.getName();
            logger.debug("Fetching transaction history for user: {}", phone);
            
            User user = userService.findByPhone(phone);
            List<Transaction> transactions = transactionService.getUserTransactions(user.getId());
            
            List<TransactionResponse> response = transactions.stream()
                .map(t -> new TransactionResponse(
                    t.getId(),
                    t.getFromWallet().getUser().getPhone(),
                    t.getToWallet().getUser().getPhone(),
                    t.getAmount(),
                    determineTransactionType(t, user.getId()),
                    t.getStatus(),
                    t.getCreatedAt()
                ))
                .collect(Collectors.toList());
            
            logger.debug("Found {} transactions for user: {}", response.size(), phone);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching transaction history for user {}: {}", authentication.getName(), e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/sent")
    public ResponseEntity<?> getSentTransactions(Authentication authentication) {
        try {
            String phone = authentication.getName();
            User user = userService.findByPhone(phone);
            List<Transaction> transactions = transactionService.getSentTransactions(user.getId());
            
            List<TransactionResponse> response = transactions.stream()
                .map(t -> new TransactionResponse(
                    t.getId(),
                    t.getFromWallet().getUser().getPhone(),
                    t.getToWallet().getUser().getPhone(),
                    t.getAmount(),
                    "SENT",
                    t.getStatus(),
                    t.getCreatedAt()
                ))
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching sent transactions for user {}: {}", authentication.getName(), e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/received")
    public ResponseEntity<?> getReceivedTransactions(Authentication authentication) {
        try {
            String phone = authentication.getName();
            User user = userService.findByPhone(phone);
            List<Transaction> transactions = transactionService.getReceivedTransactions(user.getId());
            
            List<TransactionResponse> response = transactions.stream()
                .map(t -> new TransactionResponse(
                    t.getId(),
                    t.getFromWallet().getUser().getPhone(),
                    t.getToWallet().getUser().getPhone(),
                    t.getAmount(),
                    "RECEIVED",
                    t.getStatus(),
                    t.getCreatedAt()
                ))
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching received transactions for user {}: {}", authentication.getName(), e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    private String determineTransactionType(Transaction transaction, Long userId) {
        if (transaction.getFromWallet().getUser().getId().equals(userId)) {
            return "SENT";
        } else {
            return "RECEIVED";
        }
    }
}
