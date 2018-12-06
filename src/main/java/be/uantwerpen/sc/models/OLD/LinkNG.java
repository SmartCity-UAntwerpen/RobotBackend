package be.uantwerpen.sc.models.OLD;

import javax.persistence.*;
import java.util.Objects;

/**
 * Link Data Class
 * Link between 2 points, basically the path a robot follows
 */

@Table(name = "linksng", catalog = "\"robotDB\"")
public class LinkNG
{
    private Long id;
    private Long length;
    private Double angle;
    private Point startPoint;
    private Point stopPoint;
    private int weight;
    private int trafficWeight;
    private int lockedBy;

    public LinkNG(Long id){
        this.id=id;
    }
    public LinkNG(){
        this.id=0L;
    }

    @Id
    @Column(name = "idlink")
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    @Basic
    @Column(name = "length")
    public Long getLength()
    {
        return length;
    }

    public void setLength(Long length)
    {
        this.length = length;
    }
    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        LinkNG that = (LinkNG) o;

        if(!Objects.equals(id, that.id)) return false;
        if(length != null ? !length.equals(that.length) : that.length != null) return false;
        return true;
    }

    @Override
    public int hashCode()
    {
        int result = (int)(id % Integer.MAX_VALUE);

        result = 31 * result + (length != null ? length.hashCode() : 0);

        return result;
    }

    @OneToOne
    @JoinColumn(name = "\"startpoint\"", referencedColumnName = "idpoint")
    public Point getStartPoint()
    {
        return startPoint;
    }

    public void setStartPoint(Point startPoint)
    {
        this.startPoint = startPoint;
    }

    @OneToOne
    @JoinColumn(name = "\"stoppoint\"", referencedColumnName = "idpoint")
    public Point getStopPoint()
    {
        return stopPoint;
    }

    public void setStopPoint(Point stopPoint)
    {
        this.stopPoint = stopPoint;
    }

    @Basic
    @Column(name = "\"trafficweight\"")
    public int getTrafficWeight()
    {
        return trafficWeight;
    }

    public void setTrafficWeight(int trafficWeight)
    {
        this.trafficWeight = trafficWeight;
    }

    @Basic
    @Column(name = "\"lockedby\"")
    public int getLocked()
    {
        return lockedBy;
    }

    public void setLocked(int lockedBy)
    {
        this.lockedBy = lockedBy;
    }

    @Basic
    @Column(name = "weight")
    public int getWeight()
    {
        return weight;
    }

    public void setWeight(int weight)
    {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "LinkEntity{" +
                "lid=" + id +
                ", length=" + length +
                ", startPoint=" + startPoint +
                ", stopPoint=" + stopPoint +
                ", weight=" + weight +
                '}';
    }

    @Basic
    @Column(name = "angle")
    public Double getAngle() {
        return angle;
    }

    public void setAngle(Double angle) {
        this.angle = angle;
    }
}
