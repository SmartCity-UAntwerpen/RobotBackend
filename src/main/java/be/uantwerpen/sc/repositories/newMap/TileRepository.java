package be.uantwerpen.sc.repositories.newMap;

import be.uantwerpen.sc.models.map.newMap.Tile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TileRepository extends CrudRepository<Tile, Long> {
    List<Tile> findAll();
}
