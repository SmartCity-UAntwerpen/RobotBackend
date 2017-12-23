package be.uantwerpen.sc.models;

/**
 * Cost Data Class
 * TODO
 */
public class Cost {
    private boolean status;
    private double weightToStart;
    private double weight;
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

    public double getWeightToStart()
    {
        return weightToStart;
    }

    public void setWeightToStart(double w)
    {
        this.weightToStart = w;
    }

    public double getWeight()
    {
        return weight;
    }

    public void setWeight(double w)
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
