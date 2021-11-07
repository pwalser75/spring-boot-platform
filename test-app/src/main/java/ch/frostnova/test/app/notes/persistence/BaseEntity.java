package ch.frostnova.test.app.notes.persistence;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.Version;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;

import static java.time.temporal.ChronoUnit.MILLIS;

@MappedSuperclass
public abstract class BaseEntity<ID extends Serializable> {

    @Version
    @Column(name = "VERSION", nullable = false)
    private Long version;
    @Column(name = "CREATED_ON", nullable = false)
    private OffsetDateTime created;
    @Column(name = "UPDATED_ON", nullable = false)
    private OffsetDateTime lastModified;

    protected abstract ID getId();

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
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

    public boolean isPersistent() {
        return getId() != null;
    }

    @PrePersist
    private void setAuditDates() {
        lastModified = OffsetDateTime.now().truncatedTo(MILLIS);
        if (created == null) {
            created = lastModified;
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (getId() == null) {
            return false;
        }
        BaseEntity<?> that = (BaseEntity<?>) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getClass().getName(), getId());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "#" + getId();
    }

}