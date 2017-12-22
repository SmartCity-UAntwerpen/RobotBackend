package be.uantwerpen.sc.repositories;

import be.uantwerpen.sc.models.Point;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Point Repository
 * Interface for connecting with database
 */
@Repository
public interface PointRepository extends CrudRepository<Point, Long>
{
    /**
     * Returns list of available points
     * @return List of Points
     */
    List<Point> findAll();
}
