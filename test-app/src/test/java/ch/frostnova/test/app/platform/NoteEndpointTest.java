package ch.frostnova.test.app.platform;


import ch.frostnova.test.app.notes.api.Note;
import ch.frostnova.test.app.notes.client.NoteClient;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Note endpoint test
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("performance-logging")
public class NoteEndpointTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @LocalServerPort
    private int port;

    @Test
    public void testCRUD() {

        final String baseURL = "https://localhost:" + port + "/api/notes";
        log.info("BASE URL: " + baseURL);
        try (final NoteClient noteClient = new NoteClient(baseURL)) {

            // create

            Note note = new Note();
            note.setText("Aloha");

            Note created = noteClient.create(note);
            assertThat((Object) created).isNotNull();
            assertThat((Object) created.getId()).isNotNull();
            assertThat(created.getText()).isEqualTo(note.getText());
            long id = created.getId();
            note = created;

            // read

            Note loaded = noteClient.get(id);
            assertThat((Object) loaded).isNotNull();
            assertThat((Object) loaded.getId()).isNotNull();
            assertThat(loaded.getText()).isEqualTo(note.getText());

            // list

            assertThat((Boolean) noteClient.list().stream().anyMatch(p1 -> p1.getId() == id)).isTrue();

            // update

            note.setText("Lorem ipsum dolor sit amet");
            noteClient.save(note);

            loaded = noteClient.get(id);
            assertThat((Object) loaded).isNotNull();
            assertThat(loaded.getId()).isEqualTo(note.getId());
            assertThat(loaded.getText()).isEqualTo(note.getText());

            // delete

            noteClient.delete(id);

            // delete again - must not result in an exception
            noteClient.delete(id);

            // must not be found afterwards
            assertThat((Boolean) noteClient.list().stream().anyMatch(p -> p.getId() == id)).isFalse();

            try {
                noteClient.get(id);
                org.junit.jupiter.api.Assertions.fail("Expected: NotFoundException");
            } catch (NotFoundException expected) {
                //
            }
        }
    }

    @Test
    public void testValidation() {
        final String baseURL = "https://localhost:" + port + "/api/notes";
        log.info("BASE URL: " + baseURL);
        try (final NoteClient noteClient = new NoteClient(baseURL)) {
            Note note = new Note();
            assertThatThrownBy(() -> noteClient.create(note)).isInstanceOf(BadRequestException.class);
        }
    }
}