package be.uantwerpen.sc.tools;

import java.io.Serializable;

/**
 * Created by Arthur on 28/04/2016.
 */
public class DriveDir{

    public DriveDir(){

    }
    public DriveDirEnum getDir() {
        return dir;
    }

    DriveDirEnum dir;

    private double angle;

    public DriveDir(DriveDirEnum dir){
        this.dir = dir;
    }

    @Override
    public String toString(){
        switch(dir){
            case FORWARD:
                return "DRIVE FORWARD 120";
            case LEFT:
                return "DRIVE TURN L";
            case RIGHT:
                return "DRIVE TURN R";
            case FOLLOW:
                return "DRIVE FOLLOWLINE";
            case TURN:
                return "DRIVE ROTATE R 180 ";
            default:
                return "MISSING";
        }
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }
}
