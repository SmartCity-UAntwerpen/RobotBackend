package be.uantwerpen.sc.repositories;

import be.uantwerpen.sc.models.Link;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository of Links
 * Interface for connecting with database
 */
@Repository
public interface LinkRepository extends CrudRepository<Link, Long>
{
    /**
     * Return list of all available Links
     * @return List of Links
     */
    List<Link> findAll();
}
