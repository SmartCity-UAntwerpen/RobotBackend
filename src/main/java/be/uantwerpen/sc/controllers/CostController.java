package be.uantwerpen.sc.controllers;

import be.uantwerpen.rc.models.Bot;
import be.uantwerpen.rc.models.Cost;
import be.uantwerpen.sc.services.BotControlService;
import be.uantwerpen.sc.services.PathPlanningService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author  Dries on 11-5-2017.
 * @author Reinout
 *
 * Cost Controller
 */
@RestController
@RequestMapping("/")
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
     *
     * @param start
     * @param stop
     * @return
     */
    @RequestMapping(value = "{start}/{stop}",method = RequestMethod.GET)
    public String calcCost(@PathVariable("start") int start, @PathVariable("stop") int stop)
    {
        int cost = (int) pathPlanningService.CalculatePathWeight(start, stop);
        if(botControlService.getAllAvialableBots().isEmpty()){
            cost = cost +10;
        }
        /*
        if(start == 9 && stop == 16 || start == 16 && stop == 9){
            return 1000;
        }
        if(start == 10 && stop == 16 || start == 16 && stop == 10){
            return 1000;
        }*/
        return "{\"cost\":" +cost+"}";
    }

    /*
    /**
     * HTTP function
     * Used by MAAS
     * Returns list of all bots with the weight to their start point and the weight from start to end
     * @param start
     * @param stop
     * @return
    @RequestMapping(value = "calcWeight/{start}/{stop}",method = RequestMethod.GET)
    public String calcWeight(@PathVariable("start") int start, @PathVariable("stop") int stop)
    {
        List<Bot> bots = botControlService.getAllBots();
        JSONArray array = new JSONArray();
        for (Bot b : bots) {
            JSONObject obj = new JSONObject();
            Cost c = new Cost();
            c.setIdVehicle(b.getIdCore());

            c.setWeight(pathPlanningService.CalculatePathWeight(start, stop));
            int pid = Math.toIntExact((b.getLinkId().getStartPoint().getId()));//TODO Can crash due to Link=null
            c.setWeightToStart(pathPlanningService.CalculatePathWeight(pid, start));

            try{
                obj.put("status", c.getStatus());
                obj.put("weightToStart", c.getWeightToStart());
                obj.put("weight", c.getWeight());
                obj.put("idVehicle", c.getIdVehicle());
            }catch (JSONException e) { e.printStackTrace();}
            array.put(obj);
        }
        return array.toString();
    }
        List<Bot> bots = botControlService.getAllBots();
        JSONArray array = new JSONArray();
        for (Bot b : bots) {
            JSONObject obj = new JSONObject();
            Cost c = new Cost();
            c.setIdVehicle(b.getIdCore());

            c.setWeight(pathPlanningService.CalculatePathWeight(start, stop));
            int pid = Math.toIntExact((b.getLinkId().getStartPoint().getId()));//TODO Can crash due to Link=null
            c.setWeightToStart(pathPlanningService.CalculatePathWeight(pid, start));

            try{
                obj.put("status", c.getStatus());
                obj.put("weightToStart", c.getWeightToStart());
                obj.put("weight", c.getWeight());
                obj.put("idVehicle", c.getIdVehicle());
            }catch (JSONException e) { e.printStackTrace();}
            array.put(obj);
        }
        return array.toString();
    }
*/

    /**
     * HTTP function
     * Used by MAAS
     * Returns list of all bots with the weight to their start point and the weight from start to end
     * @param start
     * @param stop
     * @return
     */
    @RequestMapping(value = "cost/calcpathweight/{start}/{stop}",method = RequestMethod.GET)
    public double calcPathWeight(@PathVariable("start") int start, @PathVariable("stop") int stop)
    {
        return pathPlanningService.CalculatePathWeight(start, stop);
    }
}
