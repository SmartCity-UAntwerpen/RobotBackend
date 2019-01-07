package be.uantwerpen.sc.repositories.newMap;

import be.uantwerpen.rc.models.Bot;
import be.uantwerpen.rc.models.map.Tile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TileRepository extends CrudRepository<Tile, Long> {
    List<Tile> findAll();
    List<Tile> findAllBylockedBy(Bot bot);
}
