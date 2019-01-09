package be.uantwerpen.sc.repositories;

import be.uantwerpen.rc.models.Bot;
import be.uantwerpen.rc.models.map.Tile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TileRepository extends CrudRepository<Tile, Long> {

    /**
     * Return a List of all Tiles
     * @return List of Tiles
     */
    List<Tile> findAll();

    /**
     * Returns a List of all Tiles currently locked by a specific bot
     * @param bot, the bot
     * @return List of Tiles
     */
    List<Tile> findAllBylockedBy(Bot bot);
}
