package be.uantwerpen.sc.repositories;

import be.uantwerpen.rc.models.map.Point;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointRepository extends CrudRepository<Point, Long> {

    /**
     * Returns a List of all points
     * @return List of Points
     */
    List<Point> findAll();
}
