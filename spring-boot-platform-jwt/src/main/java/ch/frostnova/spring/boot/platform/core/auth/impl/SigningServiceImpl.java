package ch.frostnova.spring.boot.platform.core.auth.impl;

import ch.frostnova.spring.boot.platform.core.auth.SigningService;
import ch.frostnova.spring.boot.platform.core.auth.properties.SigningProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Signature;

@Service
public class SigningServiceImpl implements SigningService {

    @Autowired
    private SigningProperties signingProperties;

    @Override
    public byte[] sign(byte[] data) throws Exception {
        if (data == null) {
            throw new IllegalArgumentException("data is required");
        }
        Signature signature = Signature.getInstance(signingProperties.requireKeyType().getSignatureAlgorithm().getJcaName());
        signature.initSign(signingProperties.requirePrivateKey());
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
        Signature signature = Signature.getInstance(signingProperties.requireKeyType().getSignatureAlgorithm().getJcaName());
        signature.initVerify(signingProperties.requirePublicKey());
        signature.update(data);
        return signature.verify(signatureBytes);
    }
}
