package com.example.study.api.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Sha512Utils {
    private static MessageDigest digest() {
        try {
            return MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public static String toBase64UrlEncoded(String source) {
        if (source == null || source.isEmpty()) {
            return "";
        }

        byte[] hashed = digest().digest(source.getBytes(StandardCharsets.UTF_8));

        return Base64.getUrlEncoder().encodeToString(hashed);
    }
}
