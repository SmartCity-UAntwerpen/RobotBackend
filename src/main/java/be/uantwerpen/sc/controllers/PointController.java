package be.uantwerpen.sc.controllers;

import be.uantwerpen.rc.models.Bot;
import be.uantwerpen.rc.models.map.Point;
import be.uantwerpen.sc.services.BotControlService;
import be.uantwerpen.sc.services.PointControlService;
import be.uantwerpen.sc.services.TrafficLightControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Null;
import java.util.List;

/**
 * @author  Niels on 23/03/2016.
 * @author Reinout
 * @author Dieter 2018-2019
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

    private Logger logger = LoggerFactory.getLogger(PointController.class);

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
     * @return success
     */
    @RequestMapping(value = "requestlock/{botId}/{id}", method = RequestMethod.GET)
    public boolean requestPointLock(@PathVariable("botId") Long botId,@PathVariable("id") Long id)
    {
        synchronized(this)
        {
            Point point = pointService.getPoint(id);

            if(point == null)//Point not found
                return false;
            try{
                if(point.getTileLock() && !point.getTile().getLockedBy().getIdCore().equals(botId)){
                    //Point already locked
                    logger.info("Bot "+botId+" was denied lock for point: "+id);
                    return false;
                } else {
                    //Point not locked -> attempt lock
                    Bot bot = botService.getBot(botId);
                    point.setTileLock(true,bot);
                    pointService.save(point);
                    logger.info("Bot "+botId+" locked point: "+id);
                    return true;
                }
            }catch (NullPointerException e){
                logger.error("Error locking point: "+id);
                return false;
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
                logger.info("Bot "+botId+" unlocked point: "+id);
                return true;
            } else {
                return false;
            }
        }catch(NullPointerException e){
            logger.error("Bot "+botId+" not found for unlocking point: "+id);
            return false;
        }
    }
}
