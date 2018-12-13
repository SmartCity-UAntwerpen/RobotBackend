package be.uantwerpen.sc.repositories.newMap;

import be.uantwerpen.rc.models.map.Link;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinkRepository extends CrudRepository<Link, Long> {
    /**
     * Return list of all available Links
     * @return List of Links
     */
     List<Link> findAll();
}
