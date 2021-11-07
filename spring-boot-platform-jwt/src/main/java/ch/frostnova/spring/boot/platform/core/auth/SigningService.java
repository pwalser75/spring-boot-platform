package ch.frostnova.spring.boot.platform.core.auth;

public interface SigningService {

    byte[] sign(byte[] data) throws Exception;

    boolean verify(byte[] data, byte[] signatureBytes) throws Exception;

}
