package be.uantwerpen.sc.services.newMap;

import be.uantwerpen.sc.models.map.newMap.Tile;
import be.uantwerpen.sc.repositories.newMap.TileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TileControlService {

    @Autowired
    private TileRepository tiles;

    /**
     * Encapsulator for getting all available points
     * @return List of Points
     */
    public List<Tile> getAllTiles()
    {
        return tiles.findAll();
    }

    /**
     * Encapsulator for getting Point by ID
     * @param id ID of point
     * @return Point of interest
     */
    public Tile getTile(Long id)
    {
        return tiles.findOne(id);
    }

    /**
     * Saves Point into Database
     * @param tile Point to save
     */
    public void save(Tile tile)
    {
        tiles.save(tile);
    }

}