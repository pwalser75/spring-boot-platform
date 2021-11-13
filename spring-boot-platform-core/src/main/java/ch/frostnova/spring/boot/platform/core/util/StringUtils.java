package ch.frostnova.spring.boot.platform.core.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class StringUtils {

    private StringUtils() {

    }

    /**
     * Calculates the SHA-256 hash of the given string.
     *
     * @param s string, required
     * @return SHA-256 hash
     */
    public static byte[] sha256(String s) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(s.getBytes(UTF_8));
        } catch (NoSuchAlgorithmException ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    /**
     * Converts a byte array to its hexadecimal (lowecase) representation.
     *
     * @param data data, required
     * @return hex string
     */
    public static String toHex(byte[] data) {
        StringBuilder result = new StringBuilder();
        for (byte b : data) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
