package be.uantwerpen.sc.models;

import javax.persistence.*;

/**
 * Point
 */
@Entity
@Table(name = "points", schema = "", catalog = "\"robotDB\"")
public class Point
{
    /**
     * Point ID
     */
    private Long id;

    /**
     * RF ID?
     * TODO
     */
    private String rfid;

    /**
     * Point Type
     * TODO: Enum
     */
    private String type;

    /**
     * Point lock state
     * TODO Enum?
     */
    private int pointLock;

    /**
     * Get Point ID
     * @return Point ID
     */
    @Id
    @Column(name = "idpoint")
    @GeneratedValue(strategy = GenerationType.AUTO)
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

        if(id != that.id) return false;
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
