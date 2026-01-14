package com.seal.seal.dto;

import java.math.BigDecimal;

public class WalletResponse {
    private Long id;
    private BigDecimal balance;
    private String status;
    private String userPhone;

    public WalletResponse() {}

    public WalletResponse(Long id, BigDecimal balance, String status, String userPhone) {
        this.id = id;
        this.balance = balance;
        this.status = status;
        this.userPhone = userPhone;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getUserPhone() { return userPhone; }
    public void setUserPhone(String userPhone) { this.userPhone = userPhone; }
}
