package be.uantwerpen.sc.services;

import be.uantwerpen.sc.models.Point;
import be.uantwerpen.sc.repositories.PointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for Points
 * TODO: Descriptive
 */
@Service
public class PointControlService
{

    /**
     * Autowired Point Repository
     */
    @Autowired
    private PointRepository pointRepository;

    /**
     * Encapsulator for getting all available points
     * @return List of Points
     */
    public List<Point> getAllPoints()
    {
        return pointRepository.findAll();
    }

    /**
     * Encapsulator for getting Point by ID
     * @param id ID of point
     * @return Point of interest
     */
    public Point getPoint(Long id)
    {
        return pointRepository.findOne(id);
    }

    /**
     * Saves Point into Database
     * @param point Point to save
     */
    public void save(Point point)
    {
         pointRepository.save(point);
    }

    /**
     * Clears all locks from the points
     */
    public void clearAllLocks()
    {
        for(Point point : pointRepository.findAll())
        {
            point.setPointLock(0);
            this.save(point);
        }
    }
}
