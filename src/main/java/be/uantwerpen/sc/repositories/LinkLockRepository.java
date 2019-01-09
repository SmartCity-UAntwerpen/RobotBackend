package be.uantwerpen.sc.repositories;
;
import be.uantwerpen.rc.models.Bot;
import be.uantwerpen.rc.models.map.LinkLock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinkLockRepository extends CrudRepository<LinkLock, Long> {

    /**
     * Returns a List of all LinkLocks that are locked by a specific bot
     * @param bot, the bot
     * @return List of LinkLocks
     */
    List<LinkLock> findAllBylockedBy(Bot bot);
}
