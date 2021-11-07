package ch.frostnova.test.app.notes.client.config;

import ch.frostnova.test.app.notes.client.NoteClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ws.rs.client.ClientBuilder;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import java.io.InputStream;
import java.security.KeyStore;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static org.glassfish.jersey.client.ClientProperties.CONNECT_TIMEOUT;
import static org.glassfish.jersey.client.ClientProperties.READ_TIMEOUT;
import static org.glassfish.jersey.logging.LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL_CLIENT;
import static org.glassfish.jersey.logging.LoggingFeature.LOGGING_FEATURE_VERBOSITY_CLIENT;
import static org.glassfish.jersey.logging.LoggingFeature.Verbosity.PAYLOAD_ANY;

/**
 * Rest client configuration, provides the {@link ClientBuilder}
 */
public final class RestClientConfig {

    private RestClientConfig() {

    }

    public static ClientBuilder clientBuilder() {

        try (InputStream in = NoteClient.class.getResourceAsStream("/client-truststore.jks")) {
            KeyStore truststore = KeyStore.getInstance(KeyStore.getDefaultType());
            truststore.load(in, "truststore".toCharArray());

            JacksonJaxbJsonProvider jsonProvider = new JacksonJaxbJsonProvider();
            jsonProvider.setMapper(objectMapper());

            return ClientBuilder.newBuilder()
                    .trustStore(truststore)
                    .property(CONNECT_TIMEOUT, 1000)
                    .property(READ_TIMEOUT, 5000)
                    .property(LOGGING_FEATURE_VERBOSITY_CLIENT, PAYLOAD_ANY)
                    .property(LOGGING_FEATURE_LOGGER_LEVEL_CLIENT, "WARNING")
                    .register(jsonProvider);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to load client truststore", ex);
        }
    }

    public static ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector());
        mapper.registerModule(new JavaTimeModule());
        mapper.setDateFormat(new StdDateFormat());
        mapper.disable(WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}