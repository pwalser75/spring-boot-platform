package ch.frostnova.test.app.notes.service;

import ch.frostnova.test.app.notes.api.Note;
import ch.frostnova.test.app.notes.api.NoteService;
import ch.frostnova.test.app.notes.api.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.List;
import java.util.UUID;

import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class NoteServiceTest {

    @Autowired
    private NoteService noteService;

    @Test
    public void shouldSaveNote() {
        String text = "Lorem ipsum dolor sit amet " + UUID.randomUUID();
        Note note = new Note();
        note.setText(text);

        Note saved = noteService.save(note);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreated()).isBeforeOrEqualTo(now());
        assertThat(saved.getLastModified()).isEqualTo(saved.getCreated());
        assertThat(saved.getText()).isEqualTo(text);

        Note loaded = noteService.get(saved.getId());
        assertThat(loaded).isEqualTo(saved);
    }

    @Test
    public void shouldFindNote() {
        Note note = new Note();
        note.setText(UUID.randomUUID().toString());
        Note saved = noteService.save(note);

        List<Note> allNotes = noteService.list();
        assertThat(allNotes).contains(saved);
    }

    @Test
    public void shouldUpdateNote() {
        Note note = new Note();
        note.setText(UUID.randomUUID().toString());
        note = noteService.save(note);

        note.setText("Changed");
        noteService.save(note);

        note = noteService.get(note.getId());
        assertThat(note.getCreated()).isBeforeOrEqualTo(now());
        assertThat(note.getLastModified()).isAfterOrEqualTo(note.getCreated());
        assertThat(note.getText()).isEqualTo("Changed");
    }

    @Test
    public void shouldDeleteNote() {
        Note note = new Note();
        note.setText(UUID.randomUUID().toString());
        note = noteService.save(note);
        Long id = note.getId();
        assertThat(noteService.list()).contains(note);

        noteService.delete(id);
        assertThat(noteService.list()).doesNotContain(note);
        assertThatThrownBy(() -> noteService.get(id)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void shouldNotSaveNoteInvalidMissingText() {

        Note note = new Note();
        assertThatThrownBy(() -> noteService.save(note))
                .isInstanceOfSatisfying(ConstraintViolationException.class, ex ->
                        assertThat(ex.getConstraintViolations())
                                .extracting(ConstraintViolation::getPropertyPath)
                                .map(Path::toString)
                                .containsExactly("save.note.text"));
    }

    @Test
    public void shouldNotSaveNoteInvalidTextTooLong() {

        Note note = new Note();
        note.setText("ABCDE".repeat(1000));
        assertThatThrownBy(() -> noteService.save(note))
                .isInstanceOfSatisfying(ConstraintViolationException.class, ex ->
                        assertThat(ex.getConstraintViolations())
                                .extracting(ConstraintViolation::getPropertyPath)
                                .map(Path::toString)
                                .containsExactly("save.note.text"));
    }
}