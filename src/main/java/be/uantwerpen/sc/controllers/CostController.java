package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.controllers.mqtt.MqttJobPublisher;
import be.uantwerpen.sc.models.*;
import be.uantwerpen.sc.models.map.MapJson;
import be.uantwerpen.sc.services.BotControlService;
import be.uantwerpen.sc.services.LinkControlService;
import be.uantwerpen.sc.services.PathPlanningService;
import be.uantwerpen.sc.services.PointControlService;
import be.uantwerpen.sc.tools.Edge;
import be.uantwerpen.sc.tools.Vertex;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author  Dries on 11-5-2017.
 * @author Reinout
 *
 * Cost Controller
 * TODO uses, working?
 */
@RestController
@RequestMapping("/cost/")
public class CostController {

    /**
     * Autowired path planning service
     */
    @Autowired
    private PathPlanningService pathPlanningService;

    /**
     * Autowired Bot Control Service
     */
    @Autowired
    private BotControlService botControlService;

    /**
     * Autowired Point Control Service
     */
    @Autowired
    private PointControlService pointControlService;

    /**
     * Autowired Link Control Service
     */
    @Autowired
    private LinkControlService linkControlService;

    /**
     * Core IP
     * TODO: Why static? Perhaps server
     */
    String coreIp = "143.129.39.151";
    /**
     * Core Port
     * TODO: Why static?
     */
    String corePort = "10000";

    /**
     * HTTP function
     * Used by TODO
     * Assumedly calculates best ID of vehicle that has the lowest cost for given values
     * TODO Test & Check if assumption is true
     * @param start
     * @param stop
     * @return
     */
    @RequestMapping(value = "calcWeight/{start}/{stop}",method = RequestMethod.GET)
    public String calcWeight(@PathVariable("start") int start, @PathVariable("stop") int stop)
    {
        List<Bot> bots = botControlService.getAllBots();
        List<Link> links = linkControlService.getAllLinks();
        List<Point> points = pointControlService.getAllPoints();
        JSONArray array = new JSONArray();
        for (Bot b : bots) {
            JSONObject obj = new JSONObject();
            long weightFromStartToStop = 0;
            long weightToStart = 0;
            Cost c = new Cost();
            c.setIdVehicle(b.getIdCore());

            List<Vertex> vertices1 = pathPlanningService.Calculatepath(start, stop, links);
            List<Point> points1 = new ArrayList<>();
            for (Vertex v1 : vertices1){
                points1.add(points.get(v1.getId().intValue()));
            }

            int size1 = vertices1.size();
            for (int i = 0; i<size1-1; i++){
                for(Link l1 : links){
                    if(l1.getStartPoint().getId()== points1.get(i).getId()-1 && l1.getStopPoint().getId()==points1.get(i+1).getId()-1){
                        weightFromStartToStop = weightFromStartToStop + (l1.getWeight()*l1.getLength()) +  l1.getTrafficWeight();
                    }
                }
            }

            Long w1 = new Long(weightFromStartToStop);
            c.setWeight(w1);

            long l = b.getLinkId().getStartPoint().getId();
            int linkId = (int) (l);
            List<Vertex> vertices2 = pathPlanningService.Calculatepath(linkId, start, links);
            List<Point> points2 = new ArrayList<>();
            for (Vertex v2 : vertices2){
                points2.add(points.get(v2.getId().intValue()));
            }

            int size2 = vertices2.size();
            for (int i = 0; i<size2-1; i++){
                for(Link l2 : links){
                    if(l2.getStartPoint().getId()== points2.get(i).getId()-1 && l2.getStopPoint().getId()==points2.get(i+1).getId()-1){
                        weightToStart = weightToStart + (l2.getWeight()*l2.getLength()) +  l2.getTrafficWeight();
                    }
                }
            }

            Long w2 = new Long(weightToStart);
            c.setWeightToStart(w2);

            try{
                obj.put("status", c.getStatus());
                obj.put("weightToStart", c.getWeightToStart());
                obj.put("weight", c.getWeight());
                obj.put("idVehicle", c.getIdVehicle());
            }catch (JSONException e) { }
            array.put(obj);

        }
        return array.toString();
    }

}
