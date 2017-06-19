package be.uantwerpen.sc.models;

import javax.persistence.*;

/**
 * Created by Niels on 24/03/2016.
 */
@Entity
@Table(name = "robots", schema = "", catalog = "\"robotDB\"")
public class Bot
{
    private Long id;
    private Long idCore;
    private Long idStart;
    private Long idStop;
    private Long jobId;
    private Long travelledDistance;
    private Integer percentageCompleted;
    private String state;
    private int busy;
    private Link link;
    private boolean alive;

    @Id
    @Column(name = "\"id\"")
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
    @Column(name = "\"idstart\"")
    public Long getIdStart()
    {
        return idStart;
    }

    public void setIdStart(Long idStart)
    {
        this.idStart = idStart;
    }

    @Basic
    @Column(name = "\"idstop\"")
    public Long getIdStop()
    {
        return idStop;
    }

    public void setIdStop(Long idStop)
    {
        this.idStop = idStop;
    }

    @Basic
    @Column(name = "\"idcore\"")
    public Long getIdCore()
    {
        return idCore;
    }

    public void setIdCore(Long idCore)
    {
        this.idCore = idCore;
    }

    @Basic
    @Column(name = "\"jobid\"")
    public Long getJobId()
    {
        return jobId;
    }

    public void setJobId(Long jobId)
    {
        this.jobId = jobId;
    }

    @Basic
    @Column(name = "\"linkprogress\"")
    public Integer getPercentageCompleted()
    {
        return percentageCompleted;
    }

    public void setPercentageCompleted(Integer percentageCompleted)
    {
        this.percentageCompleted = percentageCompleted;
    }

    @Basic
    @Column(name = "\"busy\"")
    public int getBusy() {return busy;}

    public void setBusy(int busy) {this.busy = busy;}

    @Basic
    @Column(name = "\"workingmode\"")
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

        Bot that = (Bot) o;

        if(id != that.id) return false;
        if(jobId != null ? !jobId.equals(that.jobId) : that.jobId != null) return false;
        if(percentageCompleted != null ? !percentageCompleted.equals(that.percentageCompleted) : that.percentageCompleted != null)
            return false;
        //if(state != null ? !state.equals(that.state) : that.state != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = (int)(id % Integer.MAX_VALUE);

        result = 31 * result + (jobId != null ? jobId.hashCode() : 0);
        result = 31 * result + (percentageCompleted != null ? percentageCompleted.hashCode() : 0);
        //result = 31 * result + (state != null ? state.hashCode() : 0);

        return result;
    }

    @OneToOne
    @JoinColumn(name = "\"link\"", referencedColumnName = "idlink")
    public Link getLinkId()
    {
        return link;
    }

    public void setLinkId(Link link)
    {
        this.link = link;
    }

    public boolean getAlive(){
        return alive;
    }

    public void setAlive(boolean alive){
        this.alive = alive;
    }
}
