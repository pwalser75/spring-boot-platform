package ch.frostnova.test.app.notes.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import static ch.frostnova.test.app.notes.persistence.NoteEntity.SEQUENCE_NAME;
import static javax.persistence.GenerationType.SEQUENCE;

/**
 * Note Entity.
 */
@Entity
@Table(name = "NOTE")
@SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 100)
public class NoteEntity extends BaseEntity<Long> {

    static final String SEQUENCE_NAME = "NOTE_SEQ";

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = SEQUENCE_NAME)
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @Column(name = "TEXT", length = 2048, nullable = false)
    private String text;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}