package be.uantwerpen.sc.repositories.newMap;
import be.uantwerpen.rc.models.Bot;
import be.uantwerpen.rc.models.Job;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends CrudRepository<Job, Long> {

    @Override
    List<Job> findAll();
    List<Job> findAllByBot(Bot bot);
    void deleteAllByBot(Bot bot);
}
