package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.models.Bot;
import be.uantwerpen.sc.models.Link;
import be.uantwerpen.sc.models.Point;
import be.uantwerpen.sc.models.map.Map;
import be.uantwerpen.sc.models.map.MapJson;
import be.uantwerpen.sc.models.map.MapNew;
import be.uantwerpen.sc.models.map.Path;
import be.uantwerpen.sc.services.*;
import be.uantwerpen.sc.tools.DriveDir;
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
        return mapControlService.buildMap();
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
        MapJson mapJson = mapControlService.buildMapJson();

        return mapJson;
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

    /**
     * Calculates path with given start and stop ID
     * @return Generated Path
     */
    @RequestMapping(value = "getdirections/{id}", method = RequestMethod.GET)
    public DriveDir[] PathPlanning(@PathVariable("id") int id)
    {
        Bot b=botControlService.getBot((long) id);
        Path path=pathPlanningService.CalculatePath((int)(long)b.getIdStart(),(int)(long)b.getIdStop());
        List<DriveDir> dirs=pathPlanningService.createBotDriveDirs(path);
        DriveDir[] output=new DriveDir[dirs.size()];
        output=dirs.toArray(output);
        return output;
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
        return mapControlService.buildMap().toString();
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
     */
    public void updateMap()
    {
        String data =  "";
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
                data = data + output;
            }
            System.out.println(output);
            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        List<Point> points = new ArrayList<Point>();
        List<Link> links = new ArrayList<Link>();
        try{
            JSONObject object = new JSONObject(data);
            JSONArray pointList =  object.getJSONArray("pointList");
            JSONArray linkList = object.getJSONArray("linkList");

            for(int i = 0; i < pointList.length(); i++){
                JSONObject point = pointList.getJSONObject(i);
                Point p = new Point(new Long(point.getInt("id")));
                p.setRfid(point.getString("rfid"));
                p.setPointLock(point.getInt("pointLock"));
                points.add(p);
                pointControlService.save(p);
            }

            for(int j = 0; j < linkList.length(); j++){
                JSONObject link = linkList.getJSONObject(j);
                Link l = new Link(new Long(link.getInt("id")));
                l.setLength(new Long(link.getInt("length")));
                l.setStartPoint(points.get(link.getInt("startPoint")-1));
                l.setStopPoint(points.get(link.getInt("stopPoint")-1));
                l.setStartDirection(link.getString("startDirection"));
                l.setStopDirection(link.getString("stopDirection"));
                l.setWeight(link.getInt("weight"));
                links.add(l);
                linkControlService.save(l);
            }

        }catch (JSONException e) { }
    }
}
