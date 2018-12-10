package be.uantwerpen.sc.models.map.newMap;

import be.uantwerpen.sc.models.Bot;

import javax.persistence.*;

@Entity
@Table(name = "link_locks", catalog = "\"robotDB_new\"")
public class LinkLock {

    @Id
    private Long id;

    private Boolean status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="lockedBy")
    private Bot lockedBy;

    public LinkLock(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Bot getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(Bot lockedBy) {
        this.lockedBy = lockedBy;
    }
}
