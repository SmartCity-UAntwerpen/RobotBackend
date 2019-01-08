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
     * Retrieve list of all available traffic lights
     * @return List of TrafficLights
     */
    List<TrafficLight> findAll();
    TrafficLight findByPoint(Point point);
}
