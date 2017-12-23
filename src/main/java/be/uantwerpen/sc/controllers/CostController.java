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
import org.springframework.beans.factory.annotation.Value;
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
     * BackBone IP
     */
    @Value("${backbone.ip:default}")
    String backboneIP;
    /**
     * BackBone Port
     */
    @Value("${backbone.port:default}")
    String backbonePort;

    /**
     * HTTP function
     * Used by MAAS
     * Returns list of all bots with the weight to their start point and the weight from start to end
     * @param start
     * @param stop
     * @return
     */
    @RequestMapping(value = "calcWeight/{start}/{stop}",method = RequestMethod.GET)
    public String calcWeight(@PathVariable("start") int start, @PathVariable("stop") int stop)
    {
        List<Bot> bots = botControlService.getAllBots();
        JSONArray array = new JSONArray();
        for (Bot b : bots) {
            JSONObject obj = new JSONObject();
            Cost c = new Cost();
            c.setIdVehicle(b.getIdCore());

            double weightFromStartToStop=pathPlanningService.CalculatePathWeight(start, stop);
            c.setWeight(weightFromStartToStop);

            int pid = Math.toIntExact(b.getLinkId().getStartPoint().getId());
            double weightToStart = pathPlanningService.CalculatePathWeight(pid, start);
            c.setWeightToStart(weightToStart);

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


    /**
     * HTTP function
     * Used by MAAS
     * Returns list of all bots with the weight to their start point and the weight from start to end
     * @param start
     * @param stop
     * @return
     */
    @RequestMapping(value = "calcpathweight/{start}/{stop}",method = RequestMethod.GET)
    public double calcPathWeight(@PathVariable("start") int start, @PathVariable("stop") int stop)
    {
        List<Link> links = linkControlService.getAllLinks();
        return pathPlanningService.CalculatePathWeight(start, stop);
    }
}
