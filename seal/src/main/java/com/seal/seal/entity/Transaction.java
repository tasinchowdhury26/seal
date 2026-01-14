package com.seal.seal.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_wallet")
    private Wallet fromWallet;

    @ManyToOne
    @JoinColumn(name = "to_wallet")
    private Wallet toWallet;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 20)
    private String type; // SEND / RECEIVE

    @Column(length = 20)
    private String status = "SUCCESS";

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Transaction() {}

    public Transaction(Wallet fromWallet, Wallet toWallet, BigDecimal amount, String type) {
        this.fromWallet = fromWallet;
        this.toWallet = toWallet;
        this.amount = amount;
        this.type = type;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Wallet getFromWallet() { return fromWallet; }
    public void setFromWallet(Wallet fromWallet) { this.fromWallet = fromWallet; }

    public Wallet getToWallet() { return toWallet; }
    public void setToWallet(Wallet toWallet) { this.toWallet = toWallet; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
