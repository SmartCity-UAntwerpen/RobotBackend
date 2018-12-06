package be.uantwerpen.sc.models.map.newMap;

import be.uantwerpen.sc.models.Bot;

import javax.persistence.*;

@Entity
@Table(name = "tiles", catalog = "\"robotDB_1\"")
public class Tile {

    @Id
    private Long id;

    private String rfid;

    private boolean isLocked;

    private String type;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="lockedBy")
    private Bot lockedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public boolean getLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Bot getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(Bot lockedBy) {
        this.lockedBy = lockedBy;
    }
}
