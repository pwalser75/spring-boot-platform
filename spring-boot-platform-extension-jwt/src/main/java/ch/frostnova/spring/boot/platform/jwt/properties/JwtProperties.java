package ch.frostnova.spring.boot.platform.jwt.properties;

import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

@Component
@ConfigurationProperties("ch.frostnova.platform.security.jwt")
public class JwtProperties {

    private final static Pattern PEM_PRIVATE_KEY_PATTERN = Pattern.compile("(?m)(?s)^---*BEGIN.*---*$(.*)^---*END.*---*$.*");
    private final static String[] KEY_ALGORITHMS = {"EC", "RSA", "DSA"};
    private final static String UNSUPPORTED_KEY_TYPE_MESSAGE = "Unsupported private key type, only EC, RSA and DSA are supported";

    private final static String DEFAULT_CLAIM_TENANT = "tenant";
    private final static String DEFAULT_CLAIM_ROLES = "scope";

    private String publicKey;
    private String privateKey;
    private String issuer;

    private String claimTenant;
    private String claimRoles;

    private PrivateKey resolvedPrivateKey;
    private PublicKey resolvedPublicKey;
    private SignatureAlgorithm signatureAlgorithm;

    @PostConstruct
    private void init() throws IOException, NoSuchAlgorithmException {
        if (privateKey != null) {
            resolvedPrivateKey = loadPrivateKey(getResource(privateKey));
            signatureAlgorithm = signatureAlgorithmFor(resolvedPrivateKey);
        }
        if (publicKey != null) {
            resolvedPublicKey = loadPublicKey(getResource(publicKey));
            signatureAlgorithm = signatureAlgorithmFor(resolvedPublicKey);
        }
        if (resolvedPrivateKey != null && resolvedPublicKey != null) {
            if (!resolvedPrivateKey.getAlgorithm().equalsIgnoreCase(resolvedPublicKey.getAlgorithm())) {
                throw new IllegalArgumentException(String.format("Key type mismatch: private key (%s) and public key (%s) use different algorithms",
                        resolvedPrivateKey.getAlgorithm(), resolvedPublicKey.getAlgorithm()));
            }
        }
    }

    private SignatureAlgorithm signatureAlgorithmFor(Key key) throws NoSuchAlgorithmException {
        String algorithm = key.getAlgorithm();
        if (algorithm.equalsIgnoreCase("RSA")) {
            return SignatureAlgorithm.RS256;
        }
        if (algorithm.equalsIgnoreCase("EC")) {
            return SignatureAlgorithm.ES256;
        }
        throw new NoSuchAlgorithmException(String.format("No supported SignatureAlgorithm found for key type '%s'", algorithm));
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getClaimTenant() {
        return Optional.ofNullable(claimTenant).filter(Strings::isNotBlank).orElse(DEFAULT_CLAIM_TENANT);
    }

    public void setClaimTenant(String claimTenant) {
        this.claimTenant = claimTenant;
    }

    public String getClaimRoles() {
        return Optional.ofNullable(claimRoles).filter(Strings::isNotBlank).orElse(DEFAULT_CLAIM_ROLES);
    }

    public void setClaimRoles(String claimRoles) {
        this.claimRoles = claimRoles;
    }

    public PrivateKey getResolvedPrivateKey() {
        return resolvedPrivateKey;
    }

    public void setResolvedPrivateKey(PrivateKey resolvedPrivateKey) {
        this.resolvedPrivateKey = resolvedPrivateKey;
    }

    public PublicKey getResolvedPublicKey() {
        return resolvedPublicKey;
    }

    public void setResolvedPublicKey(PublicKey resolvedPublicKey) {
        this.resolvedPublicKey = resolvedPublicKey;
    }

    public void setSignatureAlgorithm(SignatureAlgorithm signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

    public PrivateKey requirePrivateKey() {
        return Optional.ofNullable(resolvedPrivateKey).orElseThrow(() -> new UnsupportedOperationException("Signing not possible - no private key configured"));
    }

    public PublicKey requirePublicKey() {
        return Optional.ofNullable(resolvedPublicKey).orElseThrow(() -> new UnsupportedOperationException("Verification not possible - no public key configured"));
    }

    public SignatureAlgorithm getSignatureAlgorithm() {
        return signatureAlgorithm;
    }


    private static PrivateKey loadPrivateKey(URL resource) {
        return loadKey(resource, PKCS8EncodedKeySpec::new, (kf, ks) -> kf.generatePrivate(ks));
    }

    private static PublicKey loadPublicKey(URL resource) {
        return loadKey(resource, X509EncodedKeySpec::new, (kf, ks) -> kf.generatePublic(ks));
    }

    interface KeyGenerator<E extends EncodedKeySpec, P extends Key> {
        P generate(KeyFactory keyFactory, E encodedKeySpec) throws InvalidKeySpecException;
    }

    private static <E extends EncodedKeySpec, P extends Key> P loadKey(URL resource,
                                                                       Function<byte[], E> encodedKeySpec,
                                                                       KeyGenerator<E, P> keyGenerator) {
        if (resource == null) {
            throw new IllegalArgumentException("resource is required");
        }
        try {
            E keySpec = encodedKeySpec.apply(loadPEM(resource, PEM_PRIVATE_KEY_PATTERN));
            for (String algorithm : KEY_ALGORITHMS) {

                KeyFactory kf = KeyFactory.getInstance(algorithm);
                try {
                    return keyGenerator.generate(kf, keySpec);
                } catch (InvalidKeySpecException ex) {

                }
            }
            throw new NoSuchAlgorithmException(UNSUPPORTED_KEY_TYPE_MESSAGE);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to load private key: " + resource, ex);
        }
    }

    private static byte[] loadPEM(URL resource, Pattern pattern) throws IOException {
        try (InputStream in = resource.openStream()) {
            String pem = new String(in.readAllBytes(), ISO_8859_1);
            String encoded = pattern.matcher(pem).replaceFirst("$1");
            return Base64.getMimeDecoder().decode(encoded);
        } catch (Exception ex) {
            throw new IOException("Could not read PEM from " + resource, ex);
        }
    }

    private URL getResource(String resourcePath) throws IOException {
        // attempt to locate Java resource
        URL resource = getClass().getResource("/" + resourcePath);
        if (resource != null) {
            return resource;
        }

        // attempt to locate file
        File file = new File(resourcePath);
        if (file.exists()) {
            if (file.isFile() && !file.canRead()) {
                throw new IOException("File is not readable: " + resourcePath);
            }
            return file.toURI().toURL();
        }
        throw new FileNotFoundException(String.format("Resource '%s' not found", resourcePath));
    }
}
