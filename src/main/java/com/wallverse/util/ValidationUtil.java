package com.wallverse.util;

public class ValidationUtil {

    public static String validateLogin(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return "Username is required.";
        }
        if (password == null || password.trim().isEmpty()) {
            return "Password is required.";
        }
        return null;
    }

    public static String validateSignup(String username, String email, String password) {
        if (username == null || username.trim().length() < 3) {
            return "Username must be at least 3 characters.";
        }
        if (email == null || !email.contains("@")) {
            return "Email must contain @.";
        }
        if (password == null || password.length() < 6) {
            return "Password must be at least 6 characters.";
        }
        return null;
    }
}
