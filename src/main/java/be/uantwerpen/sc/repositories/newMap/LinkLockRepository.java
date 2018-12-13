package be.uantwerpen.sc.repositories.newMap;
;
import be.uantwerpen.rc.models.map.LinkLock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkLockRepository extends CrudRepository<LinkLock, Long> {
}
