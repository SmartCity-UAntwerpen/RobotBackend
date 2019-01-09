package be.uantwerpen.sc.repositories;
import be.uantwerpen.rc.models.Bot;
import be.uantwerpen.rc.models.Job;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends CrudRepository<Job, Long> {

    /**
     * Returns a list of all Jobs
     * @return List of Jobs
     */
    @Override
    List<Job> findAll();

    /**
     * Returns a List of all jobs currently being executed by a bot (this should return only one job)
     * @param bot, the bot
     * @return List of Jobs (list with one item)
     */
    List<Job> findAllByBot(Bot bot);

    /**
     * Returns a List of all jobs that are currently not being executed by any bots
     * @return List of Jobs
     */
    List<Job> findAllByBotNull();

}
