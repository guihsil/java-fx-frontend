package com.freelamarket.util;

import com.freelamarket.model.User;

public class UserSession {

    private static UserSession instance;
    private String token;
    private String email;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) { instance = new UserSession(); }
        return instance;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void cleanUserSession() {
        this.token = null;
        this.email = null;
    }
}
