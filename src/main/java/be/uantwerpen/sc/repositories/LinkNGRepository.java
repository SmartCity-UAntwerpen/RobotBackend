package be.uantwerpen.sc.repositories;

import be.uantwerpen.sc.models.Link;
import be.uantwerpen.sc.models.LinkNG;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository of Links
 * Interface for connecting with database
 */
@Repository
public interface LinkNGRepository extends CrudRepository<LinkNG, Long>
{
    /**
     * Return list of all available Links
     * @return List of Links
     */
    List<LinkNG> findAll();
}
