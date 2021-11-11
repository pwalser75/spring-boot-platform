package ch.frostnova.test.app.notes.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Locale;
import java.util.Set;

import static ch.frostnova.spring.boot.platform.security.jackson.ObjectMappers.json;
import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for Note model
 */
public class NoteTest {

    @BeforeEach
    public void init() {
        Locale.setDefault(Locale.ENGLISH);
    }

    @Test
    public void testValidate() {

        Note note = new Note();
        note.setText("Hello World");

        // all ok
        validate(note);

        // text not empty
        note.setText(null);
        validate(note, "text");

        // text not blank
        note.setText("");
        validate(note, "text", "text");

        // text length <= 2048 chars
        StringBuilder builder = new StringBuilder();
        builder.append("#".repeat(2048));
        note.setText(builder.toString());
        validate(note);
        builder.append('#');
        note.setText(builder.toString());
        validate(note, "text");

        // dates not in the future
        note.setText("Hello World");
        note.setCreated(now().plusMinutes(1));
        note.setLastModified(now().plusDays(1));
        validate(note, "created", "lastModified");
    }

    @Test
    public void testSerialize() throws Exception {

        Note note = new Note();
        note.setText("Hello World");
        note.setId(12345L);
        note.setCreated(now().minusDays(1));
        note.setLastModified(now().minusHours(1));

        String json = json().writeValueAsString(note);
        System.out.println(json);

        Note restored = json().readValue(json, Note.class);
        assertThat(restored.getText()).isEqualTo(note.getText());
        assertThat(restored.getId()).isEqualTo(note.getId());
        assertThat(restored.getCreated().toInstant()).isEqualTo(note.getCreated().toInstant());
        assertThat(restored.getLastModified().toInstant()).isEqualTo(note.getLastModified().toInstant());

    }

    private void validate(Object obj, String... expectedErrorPropertyPaths) {

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Object>> errors = validator.validate(obj);
        errors.forEach(e -> System.out.println("- " + e.getPropertyPath() + ": " + e.getMessage()));
        assertThat(errors).extracting(ConstraintViolation::getPropertyPath).map(Path::toString).containsExactlyInAnyOrder(expectedErrorPropertyPaths);
    }
}