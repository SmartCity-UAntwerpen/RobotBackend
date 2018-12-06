package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.models.Bot;
import be.uantwerpen.sc.models.map.Path;
import be.uantwerpen.sc.models.map.newMap.Link;
import be.uantwerpen.sc.models.map.newMap.Point;
import be.uantwerpen.sc.models.map.newMap.Map;
import be.uantwerpen.sc.services.*;
import be.uantwerpen.sc.services.newMap.LinkControlService;
import be.uantwerpen.sc.services.newMap.MapControlService;
import be.uantwerpen.sc.services.newMap.PointControlService;
import be.uantwerpen.sc.tools.DriveDir;
import be.uantwerpen.sc.tools.DriveDirEncapsulator;
import be.uantwerpen.sc.tools.DriveDirEnum;
import be.uantwerpen.sc.tools.Vertex;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author  Niels on 3/04/2016.
 * @author Reinout
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

    @Autowired
    private LinkControlService linkControlService;

    @Autowired
    private BotControlService botControlService;
    /**
     * BackBone IP
     */
    @Value("${backbone.ip:default}")
    String backboneIp;
    /**
     * BackBone Port
     */
    @Value("${backbone.port:default}")
    String backbonePort;

    /**
     * Get Map
     * Updates and returns map
     * @return Map
     */
    @RequestMapping(method = RequestMethod.GET)
    public Map getMap()
    {
        updateMap();
        return mapControlService.getMap();
    }

    /*
    @RequestMapping(value = "getdirections/{start}/{end}", method = RequestMethod.GET)
    public DriveDirEncapsulator getDirections(@PathVariable("start") int start, @PathVariable("end") int end)
    {
        Path path=pathPlanningService.CalculatePath(start,end);
        List<DriveDir> dirs=pathPlanningService.createBotDriveDirs(path);
        DriveDirEncapsulator directions=new DriveDirEncapsulator();
        for(int i =0; i<dirs.size(); i++){
            directions.addDriveDir(new DriveDir(dirs.get(i)));
        }
        return directions;
    }*/
    /**
     * Calculates path with given start and stop ID, returning the drive commands for the robot
     * @return Generated Path
     */
    @RequestMapping(value = "getdirectionsng/{start}/{end}", method = RequestMethod.GET)
    public DriveDirEncapsulator getDirectionsNG(@PathVariable("start") int start, @PathVariable("end") int end)
    {
        Path path=pathPlanningService.CalculatePath(start,end);
        DriveDirEncapsulator dirs=pathPlanningService.createBotDriveDirs(path);
        return dirs;
    }

    /**
     * Calculates path with given start and stop ID
     * @param start Start Node/Vertex ID
     * @param stop Stop Node/Vertex ID
     * @return Generated Path
     */
    @RequestMapping(value = "{start}/path/{stop}", method = RequestMethod.GET)
    public Path PathPlanning(@PathVariable("start") int start, @PathVariable("stop") int stop)
    {
        return pathPlanningService.CalculatePath(start,stop);
    }

    @RequestMapping(value = "getnexthop/{start}/{current}/{end}", method = RequestMethod.GET)
    public DriveDirEncapsulator getNextHop(@PathVariable("start") int start,@PathVariable("current") int current, @PathVariable("end") int end){
        List<Vertex> vertices=(pathPlanningService.CalculatePath(start,end)).getPath();
        Path path=new Path();
        for (int i=0; i<vertices.size(); i++){
            if(vertices.get(i).getId()==current){
                path.addVertex(vertices.get(i));
                path.addVertex(vertices.get(i+1));
            }
        }
        DriveDirEncapsulator dirs=pathPlanningService.createBotDriveDirs(path);
        return dirs;
    }
    /**
     * Calculates Test path with given start and stop ID
     * TODO OH WHAT YOU DO TO ME
     * NO ONE KNOWS
     * NA NANANA NA NANANA BADUM TSS
     * @param start Start Node/Vertex ID
     * @param stop Stop Node/Vertex ID
     * @return Generated Path
     */
    @RequestMapping(value = "testpath/{start}/path/{stop}", method = RequestMethod.GET)
    public Path PathPlanning2(@PathVariable("start") int start, @PathVariable("stop") int stop)
    {
        return pathPlanningService.CalculatePathNonInterface(start,stop);
    }

    /**
     * Returns Map as String
     * GET<- TODO WHO
     * For what purpose? Map exists
     * @return
     */
    @RequestMapping(value = "stringmap", method = RequestMethod.GET)
    public String mapString()
    {
        return mapControlService.getMap().toString();
    }

    /**
     * Generates random path with given start point
     * GET <- TODO WHO
     * @param start Start Vertex ID
     * @return Generated Path
     */
    @RequestMapping(value = "random/{start}", method = RequestMethod.GET)
    public Path randomPath(@PathVariable("start") int start)
    {
        return pathPlanningService.nextRandomPath(null,start);
    }

    /**
     * Update map from DSL
     */
    public void updateMap()
    {
        //TODO fill data base with the map from the DSL (<type>.save())

    }

}