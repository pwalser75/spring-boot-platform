package ch.frostnova.test.app.notes.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * DTO for Note.
 */
@ApiModel(description = "Note")
public class Note implements Serializable {

    @ApiModelProperty("Identifier (generated)")
    @JsonProperty("id")
    private Long id;

    @ApiModelProperty("Creation timestamp (generated)")
    @JsonProperty("created")
    @PastOrPresent
    private OffsetDateTime created;

    @ApiModelProperty("Last modification timestamp (generated, ISO-8601)")
    @JsonProperty("lastModified")
    @PastOrPresent
    private OffsetDateTime lastModified;

    @NotBlank
    @Size(min = 1, max = 2048)
    @ApiModelProperty("Text of the note")
    @JsonProperty("text")
    private String text;

    public Note() {
        //
    }

    public Note(String text) {
        this.text = text;
    }

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

    public OffsetDateTime getCreated() {
        return created;
    }

    public void setCreated(OffsetDateTime created) {
        this.created = created;
    }

    public OffsetDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(OffsetDateTime lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "#" + id + ": " + text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return Objects.equals(id, note.id) && Objects.equals(created, note.created) && Objects.equals(lastModified, note.lastModified) && Objects.equals(text, note.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, created, lastModified, text);
    }
}