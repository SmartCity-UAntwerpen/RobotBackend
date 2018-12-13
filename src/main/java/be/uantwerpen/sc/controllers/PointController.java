package be.uantwerpen.sc.controllers;

import be.uantwerpen.rc.models.Bot;
import be.uantwerpen.rc.models.map.Point;
import be.uantwerpen.sc.services.BotControlService;
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

    @Autowired
    private BotControlService botService;

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
     * @param botId Bot id (lockedBy)
     * @return
     */
    @RequestMapping(value = "requestlock/{botId}/{id}", method = RequestMethod.GET)
    public boolean requestPointLock(@PathVariable("botId") Long botId,@PathVariable("id") Long id)
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
                Bot bot = botService.getBot(botId);
                point.setTileLock(true,bot);
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
     * Unlocks a point
     * @param id Point ID
     * @param botId Bot ID has to match the lockedBy id from the database
     * @return result
     */
    @RequestMapping(value = "unlock/{botId}/{id}", method = RequestMethod.GET)
    public boolean unlockPoint(@PathVariable("botId") Long botId, @PathVariable("id") Long id) {
        Point point = pointService.getPoint(id);

        if(point == null){
            return false; //Point not found
        }
        try {
            //Check if bot asking the unlock is the one that locked the point
            if (point.getTile().getLockedBy().getIdCore().equals(botId) && point.getTileLock()) {
                point.setTileLock(false, null);
                pointService.save(point);
                return true;
            } else {
                return false;
            }
        }catch(NullPointerException e){
            System.err.println("Bot not found");
            return false;
        }
    }
}
