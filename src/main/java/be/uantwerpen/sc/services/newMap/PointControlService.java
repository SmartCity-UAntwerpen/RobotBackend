package be.uantwerpen.sc.services.newMap;

import be.uantwerpen.sc.models.Bot;
import be.uantwerpen.sc.models.map.newMap.Point;
import be.uantwerpen.sc.repositories.newMap.PointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointControlService {

    @Autowired
    private PointRepository points;

    /**
     * Encapsulator for getting all available points
     * @return List of Points
     */
    public List<Point> getAllPoints()
    {
        return points.findAll();
    }

    /**
     * Encapsulator for getting Point by ID
     * @param id ID of point
     * @return Point of interest
     */
    public Point getPoint(Long id)
    {
        return points.findOne(id);
    }

    /**
     * Saves Point into Database
     * @param point Point to save
     */
    public void save(Point point)
    {
        points.save(point);
    }

    /**
     * Lock the tile of which a point belongs to
     * @param id, point id
     * @param status, lock status
     */
    public void setLock(Long id,Boolean status, Bot bot){
        this.getPoint(id).setTileLock(status,bot);
    }

    /**
     * Returns the lock status of a Tile of which a point belongs to
     * @param id, point id
     * @return, lock status
     */
    public Boolean getLock(Long id){
        return this.getPoint(id).getTileLock();
    }

    /**
     * Clears all locks from the points
     */
    public void clearAllLocks()
    {
        for(Point point : points.findAll())
        {
            point.setTileLock(false,null);
            this.save(point);
        }
    }
}
