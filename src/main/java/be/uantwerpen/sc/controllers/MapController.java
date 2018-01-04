package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.models.Bot;
import be.uantwerpen.sc.models.Link;
import be.uantwerpen.sc.models.LinkNG;
import be.uantwerpen.sc.models.Point;
import be.uantwerpen.sc.models.map.*;
import be.uantwerpen.sc.services.*;
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
    private LinkNGControlService linkNGControlService;

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

    /**
     * Get Map as JSON
     * Doesnt update and returns map
     * TODO: Why?
     * @return MapJson
     */
    @RequestMapping(value = "json", method = RequestMethod.GET)
    public MapJson getMapJson()
    {
        return mapControlService.buildMapJson();
    }

    /**
     * Get New Map
     * Updates and returns New map
     * TODO WHY NEW MAP
     * WHATS THE DIFFERENCE
     * FOR THE LOVE OF GOD
     * @return Map
     */
    @RequestMapping(value = "getmap", method = RequestMethod.GET)
    public MapNew getNewMap()
    {
        updateMap();
        return mapControlService.buildNewMap();
    }
    @RequestMapping(value = "getmapng", method = RequestMethod.GET)
    public MapNG getNewMapng()
    {
        updateMap();
        return mapControlService.buildMapNG();
    }
    /**
     * Calculates path with given start and stop ID, returning the drive commands for the robot
     * @return Generated Path
     */
    @RequestMapping(value = "getdirections/{start}/{end}", method = RequestMethod.GET)
    public DriveDirEncapsulator getDirections(@PathVariable("start") int start, @PathVariable("end") int end)
    {
        Path path=pathPlanningService.CalculatePath(start,end);
        List<DriveDirEnum> dirs=pathPlanningService.createBotDriveDirs(path);
        DriveDirEncapsulator directions=new DriveDirEncapsulator();
        for(int i =0; i<dirs.size(); i++){
            directions.addDriveDir(new DriveDir(dirs.get(i)));
        }
        return directions;
    }
    @RequestMapping(value = "getdirectionsng/{start}/{end}", method = RequestMethod.GET)
    public DriveDirEncapsulator getDirectionsNG(@PathVariable("start") int start, @PathVariable("end") int end)
    {
        PathNG path=pathPlanningService.CalculatePathNG(start,end);
        DriveDirEncapsulator dirs=pathPlanningService.createBotDriveDirsNG(path);
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
    @RequestMapping(value = "{start}/pathNG/{stop}", method = RequestMethod.GET)
    public PathNG PathPlanningNG(@PathVariable("start") int start, @PathVariable("stop") int stop)
    {
        return pathPlanningService.CalculatePathNG(start,stop);
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
        List<DriveDirEnum> dirs=pathPlanningService.createBotDriveDirs(path);
        DriveDirEncapsulator directions=new DriveDirEncapsulator();
        for(int i =0; i<dirs.size(); i++){
            directions.addDriveDir(new DriveDir(dirs.get(i)));
        }
        return directions;
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
     * Returns Map as JSON String
     * GET<- TODO WHO
     * For what purpose? MapJson exists
     * @return
     */
    @RequestMapping(value = "stringmapjson", method = RequestMethod.GET)
    public String mapStringJson()
    {
        return mapControlService.buildMapJson().toString();
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
     * Update map from BackBone
     *TODO: Future Work: Databases moeten bottom->up uitgelezen worden met steeds minder details. Momenteel wordt hij bottom->down gekopieerd
     * Todo: Timed service updating?
     */
    public void updateMap()
    {

        StringBuilder data = new StringBuilder();
        try {

            URL url = new URL("http://"+backboneIp+":"+backbonePort+"/map/stringmapjson/robot");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());

            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            while ((output = br.readLine()) != null) {
                data.append(output);
            }
            System.out.println(output);
            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        List<Point> points = new ArrayList<>();
        List<Link> links = new ArrayList<>();
        try{
            JSONObject object = new JSONObject(data.toString());
            JSONArray pointList =  object.getJSONArray("pointList");
            JSONArray linkList = object.getJSONArray("linkList");

            for(int i = 0; i < pointList.length(); i++){
                JSONObject point = pointList.getJSONObject(i);
                Point p = new Point((long) point.getInt("id"));
                p.setRfid(point.getString("rfid"));
                p.setPointLock(point.getInt("pointLock"));
                points.add(p);
                pointControlService.save(p);
            }

            for(int j = 0; j < linkList.length(); j++){
                JSONObject link = linkList.getJSONObject(j);
                Link l = new Link((long) link.getInt("id"));
                l.setLength((long) link.getInt("length"));
                l.setStartPoint(points.get(link.getInt("startPoint")-1));
                l.setStopPoint(points.get(link.getInt("stopPoint")-1));
                l.setStartDirection(link.getString("startDirection"));
                l.setStopDirection(link.getString("stopDirection"));
                l.setWeight(link.getInt("weight"));
                links.add(l);
                linkControlService.save(l);
            }

        }catch (JSONException e) { e.printStackTrace();}
        ShitMap();
    }
    public void ShitMap(){

        List<Point> points = new ArrayList<>();
        List<LinkNG> links=new ArrayList<>();
        //X1
        Point p;
        for(int i=0; i<2; i++) {
            p=new Point((long) 110+i);
            p.setRfid("04 36 8A 9A F6 1F 80");
            p.setPointLock(0);
            points.add(p);
            pointControlService.save(p);
        }
        //X2
        for(int i=0; i<8; i++) {
            p=new Point((long) 120+i);
            p.setRfid("04 84 88 8A C8 48 80");
            p.setPointLock(0);
            points.add(p);
            pointControlService.save(p);
        }
        //X3
        for(int i=0; i<2; i++) {
            p=new Point((long) 130+i);
            p.setRfid("04 18 25 9A 7F 22 80");
            p.setPointLock(0);
            points.add(p);
            pointControlService.save(p);
        }
        //X1Links
        LinkNG l = new LinkNG((long) 2111);
        l.setLength((long) 1);
        l.setStartPoint(points.get(2));
        l.setStopPoint(points.get(0));
        l.setAngle(0);
        l.setWeight(1);
        links.add(l);
        linkNGControlService.save(l);
        l = new LinkNG((long) 1011);
        l.setLength((long) 1);
        l.setStartPoint(points.get(0));
        l.setStopPoint(points.get(1));
        l.setAngle(180);
        l.setWeight(1);
        links.add(l);
        linkNGControlService.save(l);
        //X2Links
        l = new LinkNG((long) 1121);
        l.setLength((long) 1);
        l.setStartPoint(points.get(1));
        l.setStopPoint(points.get(3));
        l.setAngle(0);
        l.setWeight(1);
        links.add(l);
        linkNGControlService.save(l);
        l = new LinkNG((long) 2126);
        l.setLength((long) 1);
        l.setStartPoint(points.get(3));
        l.setStopPoint(points.get(8));
        l.setAngle(-90);
        l.setWeight(1);
        links.add(l);
        linkNGControlService.save(l);
        l = new LinkNG((long) 2623);
        l.setLength((long) 1);
        l.setStartPoint(points.get(8));
        l.setStopPoint(points.get(5));
        l.setAngle(0);
        l.setWeight(1);
        links.add(l);
        linkNGControlService.save(l);
        l = new LinkNG((long) 2324);
        l.setLength((long) 1);
        l.setStartPoint(points.get(5));
        l.setStopPoint(points.get(6));
        l.setAngle(90);
        l.setWeight(1);
        links.add(l);
        linkNGControlService.save(l);
        l = new LinkNG((long) 2522);
        l.setLength((long) 1);
        l.setStartPoint(points.get(7));
        l.setStopPoint(points.get(4));
        l.setAngle(-90);
        l.setWeight(1);
        links.add(l);
        linkNGControlService.save(l);
        l = new LinkNG((long) 2227);
        l.setLength((long) 1);
        l.setStartPoint(points.get(4));
        l.setStopPoint(points.get(9));
        l.setAngle(0);
        l.setWeight(1);
        links.add(l);
        linkNGControlService.save(l);
        l = new LinkNG((long) 2720);
        l.setLength((long) 1);
        l.setStartPoint(points.get(9));
        l.setStopPoint(points.get(2));
        l.setAngle(90);
        l.setWeight(1);
        links.add(l);
        linkNGControlService.save(l);
        //X3
        l = new LinkNG((long) 2431);
        l.setLength((long) 1);
        l.setStartPoint(points.get(6));
        l.setStopPoint(points.get(11));
        l.setAngle(0);
        l.setWeight(1);
        links.add(l);
        linkNGControlService.save(l);
        l = new LinkNG((long) 3130);
        l.setLength((long) 1);
        l.setStartPoint(points.get(11));
        l.setStopPoint(points.get(10));
        l.setAngle(180);
        l.setWeight(1);
        links.add(l);
        linkNGControlService.save(l);
        l = new LinkNG((long) 3025);
        l.setLength((long) 1);
        l.setStartPoint(points.get(10));
        l.setStopPoint(points.get(7));
        l.setAngle(0);
        l.setWeight(1);
        links.add(l);
        linkNGControlService.save(l);
        //XXLinks
        l = new LinkNG((long) 11020);
        l.setLength((long) 1);
        l.setStartPoint(points.get(0));
        l.setStopPoint(points.get(2));
        l.setAngle(0);
        l.setWeight(1);
        links.add(l);
        linkNGControlService.save(l);
        l = new LinkNG((long) 12010);
        l.setLength((long) 1);
        l.setStartPoint(points.get(2));
        l.setStopPoint(points.get(0));
        l.setAngle(0);
        l.setWeight(1);
        links.add(l);
        linkNGControlService.save(l);
        l = new LinkNG((long) 13020);
        l.setLength((long) 1);
        l.setStartPoint(points.get(10));
        l.setStopPoint(points.get(2));
        l.setAngle(0);
        l.setWeight(1);
        links.add(l);
        linkNGControlService.save(l);
        l = new LinkNG((long) 12030);
        l.setLength((long) 1);
        l.setStartPoint(points.get(2));
        l.setStopPoint(points.get(10));
        l.setAngle(0);
        l.setWeight(1);
        links.add(l);
        linkNGControlService.save(l);
    }
}
