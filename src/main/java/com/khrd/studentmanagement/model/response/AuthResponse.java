package com.khrd.studentmanagement.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AuthResponse {
    private String username;
    private String token;

    public AuthResponse(UserDetails userDetails, String token) {
        this.username = userDetails.getUsername();
        this.token = token;
    }
}
