package be.uantwerpen.sc.repositories;

import be.uantwerpen.rc.models.TrafficLight;
import be.uantwerpen.rc.models.map.Point;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Traffic Light repository
 * Interface for connecting with database
 */
@Repository
public interface TrafficLightRepository extends CrudRepository<TrafficLight, Long>
{
    /**
     * Returns a List of all available traffic lights
     * @return List of TrafficLights
     */
    List<TrafficLight> findAll();

    /**
     * Returns a List of TrafficLights on a specific point (should only be one)
     * @param point, the point
     * @return List of TrafficLights (should only contain one item)
     */
    List<TrafficLight> findByPoint(Point point);
}
