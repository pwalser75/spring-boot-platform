package ch.frostnova.test.app.notes.persistence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test JPA repository
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RepositoryTest {

    @Autowired
    private NoteRepository repository;

    @Test
    public void testCRUD() {

        // create
        NoteEntity note = new NoteEntity();
        note.setText("Aloha");

        assertThat((Boolean) note.isPersistent()).isFalse();
        note = repository.save(note);
        assertThat((Boolean) note.isPersistent()).isTrue();
        assertThat((Object) note.getId()).isNotNull();

        // read
        note = repository.findById(note.getId()).orElseThrow(NoSuchElementException::new);
        assertThat(note.getText()).isEqualTo("Aloha");

        // update
        note.setText("Lorem ipsum dolor sit amet");
        note = repository.save(note);

        // delete
        repository.deleteById(note.getId());
        note = repository.findById(note.getId()).orElse(null);
        assertThat((Object) note).isNull();
    }
}