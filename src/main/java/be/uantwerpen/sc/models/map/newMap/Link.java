package be.uantwerpen.sc.models.map.newMap;


import be.uantwerpen.sc.models.Bot;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "links", catalog = "\"robotDB_new\"")
public class Link {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="start")
    private Point startPoint;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="end")
    private Point endPoint;
    private int weight;
    private double angle;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="linkLockId")
    private LinkLock lock;

    public Link(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @OneToOne
    @JoinColumn(name = "\"start\"")
    public Point getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }

    @OneToOne
    @JoinColumn(name = "\"end\"")
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

    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        Link that = (Link) o;

        if(!Objects.equals(id, that.id)) return false;

        return true;
    }
}
