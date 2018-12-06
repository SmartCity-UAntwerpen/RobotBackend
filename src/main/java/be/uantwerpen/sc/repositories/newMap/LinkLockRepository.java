package be.uantwerpen.sc.repositories.newMap;
;
import be.uantwerpen.sc.models.map.newMap.LinkLock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkLockRepository extends CrudRepository<LinkLock, Long> {
}
