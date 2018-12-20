package be.uantwerpen.sc.repositories;

import be.uantwerpen.rc.models.Bot;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Bot Repository
 * Interface for connecting with database
 */
@Repository
public interface BotRepository extends CrudRepository<Bot, Long>
{
    /**
     * Return list of all available Bots
     * @return List of Bots
     */
    List<Bot> findAll();

    List<Bot> findAllByBusyFalseAndPointNotNull();
}
