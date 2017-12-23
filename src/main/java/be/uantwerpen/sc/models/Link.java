package be.uantwerpen.sc.models;

import javax.persistence.*;
import java.util.Objects;

/**
 * Link Data Class
 * Link between 2 points, basically the path a robot follows
 */
@Entity
@Table(name = "links", catalog = "\"robotDB\"")
public class Link
{
    private Long id;
    private Long length;
    private String startDirection;
    private String stopDirection;
    private Point startPoint;
    private Point stopPoint;
    private int weight;
    private int trafficWeight;
    private int lockedBy;

    public Link(Long id){
        this.id=id;
    }
    public Link(){
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

    @Basic
    @Column(name = "\"startdir\"")
    public String getStartDirection()
    {
        return startDirection;
    }

    public void setStartDirection(String startDirection)
    {
        this.startDirection = startDirection;
    }

    @Basic
    @Column(name = "\"stopdir\"")
    public String getStopDirection()
    {
        return stopDirection;
    }

    public void setStopDirection(String stopDirection)
    {
        this.stopDirection = stopDirection;
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        Link that = (Link) o;

        if(!Objects.equals(id, that.id)) return false;
        if(length != null ? !length.equals(that.length) : that.length != null) return false;
        if(startDirection != null ? !startDirection.equals(that.startDirection) : that.startDirection != null)
            return false;
        if(stopDirection != null ? !stopDirection.equals(that.stopDirection) : that.stopDirection != null)
            return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = (int)(id % Integer.MAX_VALUE);

        result = 31 * result + (length != null ? length.hashCode() : 0);
        result = 31 * result + (startDirection != null ? startDirection.hashCode() : 0);
        result = 31 * result + (stopDirection != null ? stopDirection.hashCode() : 0);

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
                ", startDirection='" + startDirection + '\'' +
                ", stopDirection='" + stopDirection + '\'' +
                ", startPoint=" + startPoint +
                ", stopPoint=" + stopPoint +
                ", weight=" + weight +
                '}';
    }
}
