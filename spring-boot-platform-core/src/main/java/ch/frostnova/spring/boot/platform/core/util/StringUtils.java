package ch.frostnova.spring.boot.platform.core.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class StringUtils {

    private StringUtils() {

    }

    /**
     * Calculates the MD5 hash of the given string.
     * WARNING: MD5 is considered insecure, only use this for technical hashes.
     *
     * @param s string, required
     * @return MD5 hash
     */
    public static byte[] md5(String s) {
        return hash("MD5", s);
    }

    /**
     * Calculates the MD5 hash of the given string.
     *
     * @param s string, required
     * @return SHA-256 hash, Base64 encoded
     */
    public static String md5Base64(String s) {
        return Base64.getEncoder().encodeToString(md5(s));
    }

    /**
     * Calculates the SHA-256 hash of the given string.
     *
     * @param s string, required
     * @return SHA-256 hash
     */
    public static byte[] sha256(String s) {
        return hash("SHA-256", s);
    }

    /**
     * Calculates the SHA-256 hash of the given string.
     *
     * @param s string, required
     * @return SHA-256 hash, Base64 encoded
     */
    public static String sha256Base64(String s) {
        return Base64.getEncoder().encodeToString(sha256(s));
    }

    private static byte[] hash(String algorithm, String s) {
        try {
            return MessageDigest.getInstance(algorithm).digest(s.getBytes(UTF_8));
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
