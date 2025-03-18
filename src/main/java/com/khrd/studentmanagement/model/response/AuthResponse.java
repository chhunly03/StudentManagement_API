package com.khrd.studentmanagement.model.response;

import org.springframework.security.core.userdetails.UserDetails;

public class AuthResponse {
    private String username;
    private String token;

    public AuthResponse(UserDetails userDetails, String token) {
        this.username = userDetails.getUsername();
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
