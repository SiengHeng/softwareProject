package com.university.courseenrollment.demogradle.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class for password encoding operations
 * Note: In production, use the Bean defined in SecurityConfig
 */
public class PasswordEncoder {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * Encode a raw password
     */
    public static String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    /**
     * Check if raw password matches encoded password
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }

    /**
     * Generate a BCrypt encoded password for testing
     * Example usage: PasswordEncoder.generateBcrypt("password123")
     */
    public static String generateBcrypt(String password) {
        return encode(password);
    }
}
