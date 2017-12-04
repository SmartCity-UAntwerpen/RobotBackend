package be.uantwerpen.sc.models;

import javax.persistence.*;
import java.util.List;

/**
 * Job Data Class
 */
public class Job
{
    /**
     * ID of Job
     */
    private long jobId;

    /**
     * Start ID of Point(?)
     * TODO: Jobs are more variable than this
     */
    private long idStart;

    /**
     * End ID of Point(?)
     * TODO: Jobs are more variable than this
     */
    private long idEnd;

    /**
     * ID of Vehicle
     */
    private long idVehicle;

    /**
     * Job Description?
     */
    public Job()
    {
        this.jobId = 0L;
    }

    /**
     * Create Job with defined ID
     * @param jobId
     */
    public Job(Long jobId)
    {
        this.jobId = jobId;
    }

    /**
     * Get Job ID
     * @return ID of Job
     */
    public Long getJobId()
    {
        return jobId;
    }

    /**
     * Set Job ID
     * @param jobId ID of Job
     */
    public void setJobId(Long jobId)
    {
        this.jobId = jobId;
    }

    /**
     * Get Start Point ID
     * @return Start Point ID
     */
    public Long getIdStart()
    {
        return idStart;
    }

    /**
     * Set Start Point ID
     * @param idStart Start Point ID
     */
    public void setIdStart(Long idStart)
    {
        this.idStart = idStart;
    }

    /**
     * Get End Point ID
     * @return End Point ID
     */
    public Long getIdEnd()
    {
        return idEnd;
    }

    /**
     * Set End Point ID
     * @param idEnd End Point ID
     */
    public void setIdEnd(Long idEnd)
    {
        this.idEnd = idEnd;
    }

    /**
     * Get ID of related Job Vehicle
     * @return ID of Vehicle
     */
    public Long getIdVehicle()
    {
        return idVehicle;
    }

    /**
     * Set ID of Job Vehicle
     * @param idVehicle ID of vehicle
     */
    public void setIdVehicle(Long idVehicle)
    {
        this.idVehicle = idVehicle;
    }

    @Override
    public String toString()
    {
        return "Job{" +
                "jobId=" + jobId +
                '}';
    }
}
