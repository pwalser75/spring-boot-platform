package ch.frostnova.spring.boot.platform.security.service;

/**
 * Service to sign and verify arbitrary data.
 *
 * @author pwalser
 * @since 2021-11-07
 */
public interface SigningService {

    /**
     * Sign the given data using the private key provided in the application configuration.
     *
     * @param data data to sign
     * @return signature
     * @throws Exception exception if signing fails
     */
    byte[] sign(byte[] data) throws Exception;

    /**
     * Verify the signature of the given data using the public key provided in the application configuration.
     *
     * @param data           data to verify
     * @param signatureBytes signature
     * @return verification result: valid (true) or invalid (false)
     * @throws Exception exception if verification fails
     */
    boolean verify(byte[] data, byte[] signatureBytes) throws Exception;

}
