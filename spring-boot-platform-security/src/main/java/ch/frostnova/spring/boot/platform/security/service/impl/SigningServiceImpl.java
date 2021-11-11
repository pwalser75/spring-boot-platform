package ch.frostnova.spring.boot.platform.security.service.impl;

import ch.frostnova.spring.boot.platform.security.properties.JwtProperties;
import ch.frostnova.spring.boot.platform.security.service.SigningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Signature;
import java.security.SignatureException;

@Service
public class SigningServiceImpl implements SigningService {

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public byte[] sign(byte[] data) throws Exception {
        if (data == null) {
            throw new IllegalArgumentException("data is required");
        }
        Signature signature = Signature.getInstance(jwtProperties.getSignatureAlgorithm().getJcaName());
        signature.initSign(jwtProperties.requirePrivateKey());
        signature.update(data);
        return signature.sign();
    }

    @Override
    public boolean verify(byte[] data, byte[] signatureBytes) throws Exception {
        if (data == null) {
            throw new IllegalArgumentException("data is required");
        }
        if (signatureBytes == null) {
            throw new IllegalArgumentException("signatureBytes is required");
        }
        Signature signature = Signature.getInstance(jwtProperties.getSignatureAlgorithm().getJcaName());
        signature.initVerify(jwtProperties.requirePublicKey());
        signature.update(data);
        try {
            return signature.verify(signatureBytes);
        } catch (SignatureException ex) {
            return false;
        }
    }
}
