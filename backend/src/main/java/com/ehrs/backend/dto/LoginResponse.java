package com.ehrs.backend.dto;

public class LoginResponse {

    private final String token;
    private final AuthUserDto user;

    public LoginResponse(String token, AuthUserDto user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public AuthUserDto getUser() {
        return user;
    }
}
