package com.seal.seal.service;

import com.seal.seal.entity.Transaction;
import com.seal.seal.entity.Wallet;
import com.seal.seal.repository.TransactionRepository;
import com.seal.seal.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletService walletService;

    @Transactional
    public Transaction transferMoney(String fromPhone, String toPhone, BigDecimal amount) {
        logger.info("Starting money transfer: from={}, to={}, amount={}", fromPhone, toPhone, amount);
        
        // Find wallets
        Wallet fromWallet = walletService.findByUserPhone(fromPhone);
        Wallet toWallet = walletService.findByUserPhone(toPhone);

        logger.debug("Wallets found - From: {}, To: {}", fromWallet.getId(), toWallet.getId());

        // Validate wallets
        if (!walletService.isWalletActive(fromWallet)) {
            logger.error("Sender wallet is not active: {}", fromPhone);
            throw new RuntimeException("Sender wallet is not active");
        }
        if (!walletService.isWalletActive(toWallet)) {
            logger.error("Receiver wallet is not active: {}", toPhone);
            throw new RuntimeException("Receiver wallet is not active");
        }

        // Check sufficient balance
        if (!walletService.hasSufficientBalance(fromWallet, amount)) {
            logger.error("Insufficient balance for user {}: required={}, available={}", 
                        fromPhone, amount, fromWallet.getBalance());
            throw new RuntimeException("Insufficient balance");
        }

        // Perform transfer
        BigDecimal newFromBalance = fromWallet.getBalance().subtract(amount);
        BigDecimal newToBalance = toWallet.getBalance().add(amount);

        fromWallet.setBalance(newFromBalance);
        fromWallet.setUpdatedAt(LocalDateTime.now());
        
        toWallet.setBalance(newToBalance);
        toWallet.setUpdatedAt(LocalDateTime.now());

        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);

        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setFromWallet(fromWallet);
        transaction.setToWallet(toWallet);
        transaction.setAmount(amount);
        transaction.setType("SEND");
        transaction.setStatus("SUCCESS");
        transaction.setCreatedAt(LocalDateTime.now());

        Transaction savedTransaction = transactionRepository.save(transaction);
        
        logger.info("Money transfer completed successfully: transactionId={}, from={}, to={}, amount={}", 
                   savedTransaction.getId(), fromPhone, toPhone, amount);
        
        return savedTransaction;
    }

    public List<Transaction> getUserTransactions(Long userId) {
        logger.debug("Fetching all transactions for user: {}", userId);
        return transactionRepository.findByUserId(userId);
    }

    public List<Transaction> getSentTransactions(Long userId) {
        logger.debug("Fetching sent transactions for user: {}", userId);
        return transactionRepository.findByFromWalletUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Transaction> getReceivedTransactions(Long userId) {
        logger.debug("Fetching received transactions for user: {}", userId);
        return transactionRepository.findByToWalletUserIdOrderByCreatedAtDesc(userId);
    }
}
