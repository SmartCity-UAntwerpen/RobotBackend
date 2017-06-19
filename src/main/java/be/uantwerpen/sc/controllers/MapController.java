package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.models.Link;
import be.uantwerpen.sc.models.Point;
import be.uantwerpen.sc.models.map.Map;
import be.uantwerpen.sc.models.map.MapJson;
import be.uantwerpen.sc.models.map.MapNew;
import be.uantwerpen.sc.models.map.Path;
import be.uantwerpen.sc.services.*;
import be.uantwerpen.sc.tools.Vertex;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Created by Niels on 3/04/2016.
 */
@RestController
@RequestMapping(value = "/map/")
public class MapController
{
    @Autowired
    private MapControlService mapControlService;

    @Autowired
    private PathPlanningService pathPlanningService;

    @Autowired
    private PointControlService pointControlService;

    @Autowired
    private LinkControlService linkControlService;

    String coreIp = "143.129.39.151";
    String corePort = "10000";

    @RequestMapping(method = RequestMethod.GET)
    public Map getMap()
    {
        updateMap();
        return mapControlService.buildMap();
    }

    @RequestMapping(value = "json", method = RequestMethod.GET)
    public MapJson getMapJson()
    {
        MapJson mapJson = mapControlService.buildMapJson();

        return mapJson;
    }

    @RequestMapping(value = "getMap", method = RequestMethod.GET)
    public MapNew getNewMap()
    {
        updateMap();
        MapNew mapNew = mapControlService.buildNewMap();
        return mapNew;
    }

    @RequestMapping(value = "{start}/path/{stop}", method = RequestMethod.GET)
    public Path PathPlanning(@PathVariable("start") int start, @PathVariable("stop") int stop)
    {
        List<Link> links = new ArrayList<>();
        List<Vertex> path = pathPlanningService.Calculatepath(start,stop, links);

        return new Path(path);
    }

    @RequestMapping(value = "testpath/{start}/path/{stop}", method = RequestMethod.GET)
    public Path PathPlanning2(@PathVariable("start") int start, @PathVariable("stop") int stop)
    {
        List<Vertex> path = pathPlanningService.CalculatepathNonInterface(start,stop);
        return new Path(path);
    }

    @RequestMapping(value = "stringmapjson", method = RequestMethod.GET)
    public String mapStringJson()
    {
        return mapControlService.buildMapJson().toString();
    }

    @RequestMapping(value = "stringmap", method = RequestMethod.GET)
    public String mapString()
    {
        return mapControlService.buildMap().toString();
    }

    @RequestMapping(value = "random/{start}", method = RequestMethod.GET)
    public Path randomPath(@PathVariable("start") int start)
    {
        List<Link> links = new ArrayList<>();
        List<Vertex> vertexes = pathPlanningService.nextRandomPath(null,start, links);
        Path pathClass = new Path(vertexes);

        return pathClass;
    }

    public void updateMap()
    {
        String data =  "";
        try {

            URL url = new URL("http://"+coreIp+":"+corePort+"/map/stringmapjson/robot");
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
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                data = data + output;
                System.out.println(output);
            }

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
                Point p = new Point();
                p.setId(new Long(point.getInt("id")));
                p.setRfid(point.getString("rfid"));
                p.setPointLock(point.getInt("pointLock"));
                System.out.println(p.toString());
                points.add(p);
                pointControlService.save(p);
            }

            for(int j = 0; j < linkList.length(); j++){
                JSONObject link = linkList.getJSONObject(j);
                Link l = new Link();
                l.setId(new Long(link.getInt("id")));
                l.setLength(new Long(link.getInt("length")));
                l.setStartPoint(points.get(link.getInt("startPoint")-1));
                l.setStopPoint(points.get(link.getInt("stopPoint")-1));
                l.setStartDirection(link.getString("startDirection"));
                l.setStopDirection(link.getString("stopDirection"));
                l.setWeight(link.getInt("weight"));
                System.out.println(l.toString());
                links.add(l);
                linkControlService.save(l);
            }

        }catch (JSONException e) { }

    }
}
