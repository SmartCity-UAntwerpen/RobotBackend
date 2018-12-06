package be.uantwerpen.sc.models.OLD;

import javax.persistence.*;
import java.util.Objects;

/**
 * Point
 */

@Table(name = "points", catalog = "\"robotDB\"")
public class Point
{
    /**
     * Point ID
     */
    private Long id;

    /**
     * RFID
     * RFID of tag on crossroad
     */
    private String rfid;

    /**
     * Point Type
     * TODO: Enum
     */
    private String type;

    /**
     * Point lock state
     */
    private int pointLock;

    public Point(Long id){
        this.id=id;
    }
    public Point(){
        this.id=0L;
    }
    /**
     * Get Point ID
     * @return Point ID
     */
    @Id
    @Column(name = "idpoint")
    public Long getId()
    {
        return id;
    }

    /**
     * Set Point ID
     * @param id Point ID
     */
    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * Get Point RFID
     * @return Point RFID
     */
    @Basic
    @Column(name = "rfid")
    public String getRfid()
    {
        return rfid;
    }

    /**
     * Set Point RFID
     * @param rfid Point RFID
     */
    public void setRfid(String rfid)
    {
        this.rfid = rfid;
    }

    /**
     * Get Point Type
     * @return Point Type
     */
    @Basic
    @Column(name = "type")
    public String getType()
    {
        return type;
    }

    /**
     * Set Point Type
     * @param type Point Type
     */
    public void setType(String type)
    {
        this.type = type;
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        Point that = (Point) o;

        if(!Objects.equals(id, that.id)) return false;
        if(rfid != null ? !rfid.equals(that.rfid) : that.rfid != null) return false;
        if(type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = (int)(id % Integer.MAX_VALUE);

        result = 31 * result + (rfid != null ? rfid.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);

        return result;
    }

    /**
     * Gets Point Lock State
     * @return Point Lock State
     */
    @Basic
    @Column(name = "\"lockedby\"")
    public int getPointLock()
    {
        return pointLock;
    }

    /**
     * Set Point Lock State
     * @param pointLock Point Lock State
     */
    public void setPointLock(int pointLock)
    {
        this.pointLock = pointLock;
    }

    @Override
    public String toString()
    {
        return "PointEntity{" +
                "id=" + id +
                ", rfid='" + rfid + '\'' +
                ", type='" + type + '\'' +
                ", pointLock=" + pointLock +
                '}';
    }
}
