package com.seal.seal.service;

import com.seal.seal.entity.Wallet;
import com.seal.seal.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    public Wallet findByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user"));
    }

    public Wallet findByUserPhone(String phone) {
        return walletRepository.findByUserPhone(phone)
                .orElseThrow(() -> new RuntimeException("Wallet not found for phone: " + phone));
    }

    @Transactional
    public void updateBalance(Long walletId, BigDecimal newBalance) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        
        wallet.setBalance(newBalance);
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);
    }

    public boolean hasSufficientBalance(Wallet wallet, BigDecimal amount) {
        return wallet.getBalance().compareTo(amount) >= 0;
    }

    public boolean isWalletActive(Wallet wallet) {
        return "ACTIVE".equals(wallet.getStatus()) && "ACTIVE".equals(wallet.getUser().getStatus());
    }
}
