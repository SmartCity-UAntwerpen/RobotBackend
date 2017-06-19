package be.uantwerpen.sc.models;

/**
 * Created by Dries on 11-5-2017.
 */
public class Cost {
    private boolean status;
    private long weightToStart;
    private long weight;
    private long idVehicle;

    public Cost(){

    }

    public boolean getStatus()
    {
        return status;
    }

    public void setStatus(boolean st)
    {
        this.status = st;
    }

    public Long getWeightToStart()
    {
        return weightToStart;
    }

    public void setWeightToStart(Long w)
    {
        this.weightToStart = w;
    }

    public Long getWeight()
    {
        return weight;
    }

    public void setWeight(Long w)
    {
        this.weight = w;
    }

    public Long getIdVehicle()
    {
        return idVehicle;
    }

    public void setIdVehicle(Long id)
    {
        this.idVehicle = id;
    }
}
