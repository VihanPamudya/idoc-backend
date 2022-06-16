package com.iterminal.ndis.util;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class HashUtil {

    public static String generateHash(String hashingString) {
        String hashed = Hashing.sha256()
                .hashString(hashingString, StandardCharsets.UTF_8)
                .toString();
        return hashed;
    }
}
