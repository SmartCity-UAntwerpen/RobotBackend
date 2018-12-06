package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.models.map.newMap.Point;
import be.uantwerpen.sc.services.newMap.PointControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author  Niels on 23/03/2016.
 * @author Reinout
 * HTTP INTERFACE
 *
 */
@RestController
@RequestMapping("/point/")
public class PointController
{
    /**
     * Autowired Point Control Service
     */
    @Autowired
    private PointControlService pointService;

    /**
     * GET <- WHO
     * Get all Points
     * @return List of Points
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<Point> allPoints()
    {
        return pointService.getAllPoints();
    }

    /**
     * GET <-WHO
     * Request Locking of point
     * @param id Point ID
     * @return
     */
    @RequestMapping(value = "requestlock/{id}", method = RequestMethod.GET)
    public boolean requestPointLock(@PathVariable("id") Long id)
    {
        synchronized(this)
        {
            Point point = pointService.getPoint(id);

            if(point == null)//Point not found
                return false;

            if(point.getTileLock()) {
                //Point already locked
                return false;
            } else {
                //Point not locked -> attempt lock
                point.setTileLock(true,null);
                pointService.save(point);
                return true;
            }
        }
    }

    /**
     * Get Point status
     * GET <- WHO? Probably Robot
     * @param id Point ID
     * @return Status
     */
    @RequestMapping(value = "getlock/{id}", method = RequestMethod.GET)
    public boolean getPointStatus(@PathVariable("id") Long id) {
        Point point = pointService.getPoint(id);

        //Point not found
        return point != null && (point.getTileLock());
    }

    /**
     * Locks / unlocks point
     * Done by who? TODO
     * @param id
     * @param value
     * @return
     */
    @RequestMapping(value = "setlock/{id}/{value}", method = RequestMethod.GET)
    public boolean setPointStatus(@PathVariable("id") Long id, @PathVariable("value") boolean value)
    {
        synchronized (this)
        {
            Point point = pointService.getPoint(id);
            if(point == null)
                return false;

            point.setTileLock(value,null);
            pointService.save(point);
            return true;
        }
    }
}
