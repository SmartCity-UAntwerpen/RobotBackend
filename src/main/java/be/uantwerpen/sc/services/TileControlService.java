package be.uantwerpen.sc.services;

import be.uantwerpen.rc.models.Bot;
import be.uantwerpen.rc.models.map.Tile;
import be.uantwerpen.sc.repositories.TileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Dieter 2018-2019
 * <p>
 * Tile Control Service
 */
@Service
public class TileControlService {

    @Autowired
    private TileRepository tiles;

    /**
     * Encapsulator for getting all available points
     *
     * @return List of Points
     */
    public List<Tile> getAllTiles() {
        return tiles.findAll();
    }

    /**
     * Encapsulator for getting Point by ID
     *
     * @param id ID of point
     * @return Point of interest
     */
    public Tile getTile(Long id) {
        return tiles.findOne(id);
    }

    /**
     * Saves Point into Database
     *
     * @param tile Point to save
     */
    public void save(Tile tile) {
        tiles.save(tile);
    }

    /**
     * Remove all tile locks from a specific bot
     *
     * @param bot, the bot
     */
    public void removeAllLocksFromBot(Bot bot) {
        List<Tile> ts = tiles.findAllBylockedBy(bot);
        for (Tile t : ts) {
            t.setLocked(false);
            t.setLockedBy(null);
            tiles.save(t);
        }
    }

}
