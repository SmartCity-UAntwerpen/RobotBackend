package be.uantwerpen.sc.models.map.newMap;

import be.uantwerpen.sc.models.Bot;

import javax.persistence.*;

@Entity
@Table(name = "link_locks", catalog = "\"robotDB_1\"")
public class LinkLock {

    @Id
    private Long id;

    private boolean status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="lockedBy")
    private Bot lockedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Bot getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(Bot lockedBy) {
        this.lockedBy = lockedBy;
    }
}
