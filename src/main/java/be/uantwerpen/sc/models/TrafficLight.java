package be.uantwerpen.sc.models;

import javax.persistence.*;
import java.util.Objects;

/**
 * Traffic Light
 */
@Entity
@Table(name = "tlights", catalog = "\"robotDB\"")
public class TrafficLight
{
    /**
     * Traffic Light ID
     */
    private Long id;
    private String direction;
    private int placeLink;
    private String state;
    private Link link;

    @Id
    @Column(name = "\"idtlight\"")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    @Basic
    @Column(name = "direction")
    public String getDirection()
    {
        return direction;
    }

    public void setDirection(String direction)
    {
        this.direction = direction;
    }

    @Basic
    @Column(name = "state")
    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        TrafficLight that = (TrafficLight) o;

        if(!Objects.equals(id, that.id)) return false;
        if(direction != null ? !direction.equals(that.direction) : that.direction != null) return false;
        if(state != null ? !state.equals(that.state) : that.state != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = (int)(id % Integer.MAX_VALUE);

        result = 31 * result + (direction != null ? direction.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);

        return result;
    }


    @OneToOne
    @JoinColumn(name = "\"link\"", referencedColumnName = "idlink")
    public Link getLink()
    {
        return link;
    }

    public void setLink(Link link)
    {
        this.link = link;
    }

    @Basic
    @Column(name = "\"linkprogress\"")
    public int getPlaceLink()
    {
        return placeLink;
    }

    public void setPlaceLink(int placeLink)
    {
        this.placeLink = placeLink;
    }
}
