package be.uantwerpen.sc.models.map.newMap;


import be.uantwerpen.sc.models.Bot;

import javax.persistence.*;

@Entity
@Table(name = "links", catalog = "\"robotDB_1\"")
public class Link {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="start")
    private Point startPoint;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="end")
    private Point endPoint;

    private int weight;

    private double angle;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="linkLockId")
    private LinkLock lock;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }

    public Point getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Point endPoint) {
        this.endPoint = endPoint;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public LinkLock getLock() {
        return lock;
    }

    public void setLock(LinkLock lock) {
        this.lock = lock;
    }

    public void lockLink(Boolean status, Bot lockedBy){
        lock.setStatus(status);
        lock.setLockedBy(lockedBy);
    }

    public Boolean getLockStatus(){
        return lock.getStatus();
    }
}
