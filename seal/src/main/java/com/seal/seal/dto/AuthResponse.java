package com.seal.seal.dto;

public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String phone;
    private String message;

    public AuthResponse() {}

    public AuthResponse(String accessToken, String phone, String message) {
        this.accessToken = accessToken;
        this.phone = phone;
        this.message = message;
    }

    public AuthResponse(String accessToken, String refreshToken, String phone, String message) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.phone = phone;
        this.message = message;
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    // Backward compatibility
    public String getToken() { return accessToken; }
    public void setToken(String token) { this.accessToken = token; }
}
