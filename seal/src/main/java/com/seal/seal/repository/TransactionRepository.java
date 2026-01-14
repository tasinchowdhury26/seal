package com.seal.seal.repository;

import com.seal.seal.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    @Query("SELECT t FROM Transaction t WHERE t.fromWallet.user.id = :userId OR t.toWallet.user.id = :userId ORDER BY t.createdAt DESC")
    List<Transaction> findByUserId(@Param("userId") Long userId);
    
    List<Transaction> findByFromWalletUserIdOrderByCreatedAtDesc(Long userId);
    List<Transaction> findByToWalletUserIdOrderByCreatedAtDesc(Long userId);
}
