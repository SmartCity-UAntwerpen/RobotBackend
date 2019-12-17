package be.uantwerpen.sc.controllers;

import be.uantwerpen.rc.models.map.Path;
import be.uantwerpen.rc.models.map.Point;
import be.uantwerpen.rc.models.map.Map;
import be.uantwerpen.rc.models.map.Tile;
import be.uantwerpen.rc.tools.DriveDirEncapsulator;
import be.uantwerpen.sc.services.*;
import be.uantwerpen.sc.services.LinkControlService;
import be.uantwerpen.sc.services.MapControlService;
import be.uantwerpen.sc.services.PointControlService;
import be.uantwerpen.sc.services.TileControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author  Niels on 3/04/2016.
 * @author Reinout
 * @author Dieter 2018-2019
 *
 * Map Controller
 */
@RestController
@RequestMapping(value = "/map/")
public class MapController
{
    /**
     * Autowired Map Control Service
     */
    @Autowired
    private MapControlService mapControlService;

    /**
     * Autowired Path Planning Service
     */
    @Autowired
    private PathPlanningService pathPlanningService;

    /**
     * Autowired Port Control Service
     */
    @Autowired
    private PointControlService pointControlService;

    /**
     * Autowired Tile Control Service
     */
    @Autowired
    private TileControlService tileControlService;

    /**
     * Autowirded Link Control Service
     */
    @Autowired
    private LinkControlService linkControlService;

    /**
     * Autowired Bot Control Service
     */
    @Autowired
    private BotControlService botControlService;

    private Logger logger = LoggerFactory.getLogger(MapController.class);

    /**
     * Get Map
     * Updates and returns map
     * @return Map
     */
    @RequestMapping(method = RequestMethod.GET)
    public Map getMap()
    {
        updateMap();
        logger.info("Map requested!");
        return mapControlService.getMap();
    }

    /**
     * Get point (test function)
     * @param id, point id
     * @return point
     */
    @RequestMapping(value = "getPoint/{id}", method = RequestMethod.GET)
    public Point getPoint(@PathVariable("id") Long id)
    {
        Point point = pointControlService.getPoint(id);
        if(point != null)
            return point;
        else return null;
    }

    /**
     * Get tile (test function)
     * @param id, tile id
     * @return tile
     */
    @RequestMapping(value = "getTile/{id}", method = RequestMethod.GET)
    public Tile getTile(@PathVariable("id") Long id)
    {
        Tile tile = tileControlService.getTile(id);
        if(tile != null)
            return tile;
        else return null;
    }

    /**
     * Calculates path with given start and stop ID, returning the drive commands for the robot
     * @return Generated Path
     */
    @RequestMapping(value = "getdirections/{start}/{end}", method = RequestMethod.GET)
    public DriveDirEncapsulator getDirectionsNG(@PathVariable("start") int start, @PathVariable("end") int end)
    {
        Path path=pathPlanningService.CalculatePath(start,end);
        return pathPlanningService.createBotDriveDirs(path);
    }

    /**
     * Calculates path with given start and stop ID
     * @param start Start Node/Vertex ID
     * @param stop Stop Node/Vertex ID
     * @return Generated Path
     */
    @RequestMapping(value = "path/{start}/{stop}", method = RequestMethod.GET)
    public Path PathPlanning(@PathVariable("start") int start, @PathVariable("stop") int stop)
    {
        return pathPlanningService.CalculatePath(start,stop);
    }

    /**
     * TODO: update to new map (used for full server navigation)
     * @param start, start point
     * @param current, current location point
     * @param end, end points
     * @return path
     */
    @RequestMapping(value = "getnexthop/{start}/{current}/{end}", method = RequestMethod.GET)
    public DriveDirEncapsulator getNextHop(@PathVariable("start") int start,@PathVariable("current") int current, @PathVariable("end") int end){
        List<Point> vertices=(pathPlanningService.CalculatePath(start,end)).getPath();
        Path path=new Path();
        for (int i=0; i<vertices.size(); i++){
            if(vertices.get(i).getId()==current)
            {
                path.addVertex(vertices.get(i));
                path.addVertex(vertices.get(i+1));
            }
        }
        return pathPlanningService.createBotDriveDirs(path);
    }

    @RequestMapping(value = "loadMap", method = RequestMethod.POST)
    //public void loadMap(@RequestBody java.util.Map<String, Object> jsonMapSQL)
    public void loadMap(@RequestBody String mapSQL)
    {
        //TODO: convert jsonMap to sql commands (if requestbody is json)
        //load sql into database
        if(!this.mapControlService.loadMap(mapSQL))
            logger.error("ERROR loading map into database!");
        else
            logger.error("Map successfully loaded into database");
    }

    /**
     * Update map from DSL
     */
    public void updateMap()
    {
        //TODO fill database with the map from the DSL (<type>.save())

    }

}