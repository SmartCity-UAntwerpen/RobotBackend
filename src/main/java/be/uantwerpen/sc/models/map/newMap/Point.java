package be.uantwerpen.sc.models.map.newMap;

import be.uantwerpen.sc.models.Bot;

import javax.persistence.*;

@Entity
@Table(name = "points", catalog = "\"robotDB_new\"")
public class Point {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="tileId")
    private Tile tile;

    public Point(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public Tile getTile() {
        return this.tile;
    }

    public void setTileLock(boolean status, Bot botId){
        tile.setLocked(status);
        tile.setLockedBy(botId);
    }

    public boolean getTileLock(){
        return tile.getLocked();
    }
}
