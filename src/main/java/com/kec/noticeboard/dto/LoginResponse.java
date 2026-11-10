package com.kec.noticeboard.dto;

public class LoginResponse {
    private boolean success;
    private String message;
    private String token;
    private String tokenType = "Bearer";
    private String username;
    private String fullName;
    private String role;

    public LoginResponse() {
    }

    public LoginResponse(boolean success, String message, String token, String username, String fullName, String role) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    public static LoginResponse failure(String message) {
        LoginResponse res = new LoginResponse();
        res.setSuccess(false);
        res.setMessage(message);
        return res;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
