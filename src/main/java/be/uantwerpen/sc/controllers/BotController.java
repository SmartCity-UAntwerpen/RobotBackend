package be.uantwerpen.sc.controllers;


import be.uantwerpen.rc.models.Bot;
import be.uantwerpen.rc.models.BotState;
import be.uantwerpen.rc.models.Location;
import be.uantwerpen.rc.models.map.Link;
import be.uantwerpen.rc.models.map.Point;
import be.uantwerpen.sc.services.BotControlService;
import be.uantwerpen.sc.services.newMap.LinkControlService;
import be.uantwerpen.sc.services.newMap.PointControlService;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import org.json.JSONObject;

/**
 * Bot Controller
 */
@RestController
@RequestMapping("/bot/")
public class BotController
{
    /**
     * Autowired Botcontrol Service
     */
    @Autowired
    private BotControlService botControlService;

    /**
     * Autowired Link Control Service
     */
    @Autowired
    private LinkControlService linkControlService;

    /**
     * Autowired Link Control Service
     */
    @Autowired
    private PointControlService pointControlService;

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

    Logger logger = LoggerFactory.getLogger(BotController.class);

    /**
     * Get All Bots
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<Bot> allBots()
    {
        return botControlService.getAllBots();
    }

    @RequestMapping(value = "{id}",method = RequestMethod.GET)
    public Bot getBot(@PathVariable("id") Long id)
    {
        return botControlService.getBot(id);
    }

    @RequestMapping(value = "{id}",method = RequestMethod.POST)
    public void saveBot(@PathVariable("id") Long id, @RequestBody Bot bot)
    {
        botControlService.saveBot(bot);
    }

    @RequestMapping(value = "{id}",method = RequestMethod.PUT)
    public void updateBot(@PathVariable("id") Long id, @RequestBody Bot bot)
    {
        botControlService.saveBot(bot);
    }

    /**
     * Robot calls this GET
     * @return
     */
    @RequestMapping(value = "newRobot/{id}", method = RequestMethod.GET)
    public Long newRobot(@PathVariable("id") Long id)
    {
        Bot bot = new Bot(id);
        System.out.println(bot);
        //Save bot in database and get bot new rid
        botControlService.saveBot(bot);

        Date date = new Date();
        System.out.println("New robot created!! - " + date.toString());

        return bot.getIdCore();
    }

    /**
     * Bot location update
     * @param id, the bot id
     * @param pid, the location = a point id
     */
    @RequestMapping(value = "{id}/locationUpdate/{pid}", method = RequestMethod.GET)
    public void locationLink(@PathVariable("id") Long id, @PathVariable("pid") Long pid)
    {
        Bot bot = botControlService.getBot(id);
        Point point;

        if(bot != null)
        {
            point = pointControlService.getPoint(pid);
            logger.info("Bot with id: " +id+" updated its location: "+pid);
            if(point != null)
            {
                bot.setPoint(point);
                botControlService.saveBot(bot);
                System.out.println(bot.getIdCore());
            }
            else
                System.out.println("Point with id: " + pid + " not found!");
        }
        else
            System.out.println("Bot with id:" + id + " not found!");
    }

    public void updateLocation(Long id, Long pointId, int progress)
    {
        Bot bot = botControlService.getBot(id);

        if(bot != null)
        {
            bot.setPoint(pointControlService.getPoint(pointId));
            bot.setPercentageCompleted(progress);
            bot.updateStatus(BotState.Alive.ordinal());
            botControlService.saveBot(bot);
        }
    }

    @RequestMapping(value = "delete/{rid}",method = RequestMethod.GET)
    public void deleteBot(@PathVariable("rid") Long rid)
    {
        logger.info("Removing bot with id: "+rid);
        botControlService.deleteBot(rid);
    }

    @RequestMapping(value = "/deleteBots",method = RequestMethod.GET)
    public void resetBots()
    {
        botControlService.deleteBots();
    }

    /**
     * Get all bot positions
     * @return
     */
    @RequestMapping(value = "posAll", method = RequestMethod.GET)
    public String posAll(){
        List<Bot> bots = botControlService.getAllBots();
        JSONArray array = new JSONArray();
        for(Bot b : bots){
            Location loc = new Location();
            loc.setVehicleID(b.getIdCore());

            if (b.getBusy()){
                loc.setStartID(b.getIdStart());
                loc.setStopID(b.getIdStop());
                loc.setPercentage((long) b.getPercentageCompleted());
            }else{
                loc.setStartID(b.getLinkId().getStartPoint().getId());
                loc.setStopID(b.getLinkId().getStartPoint().getId());
                loc.setPercentage( (long)100);
            }

            JSONObject obj = new JSONObject();
            try{
                obj.put("idVehicle", loc.getVehicleID());
                obj.put("idStart", loc.getStartID());
                obj.put("idEnd", loc.getStopID());
                obj.put("percentage", loc.getPercentage());
            }
            catch (JSONException e) {e.printStackTrace(); }
            array.put(obj);
        }
        return array.toString();
    }

    @RequestMapping(value = "checkTimer", method = RequestMethod.GET)
    public void checkTimer(){
        System.out.println("Checking Alive Bots");
        List<Bot> bots = botControlService.getAllBots();
        long currentDate=new Date().getTime();
        for(Bot b : bots){
            if(b.getStatus()!=BotState.Unknown.ordinal()){
                if(currentDate-b.getLastUpdated().getTime()>1000*60*5) {
                    b.updateStatus(BotState.Unknown.ordinal());
                    botControlService.saveBot(b);
                }
            }
            else{
                if(new Date().getTime()-b.getLastUpdated().getTime()>1000*60*5) {
                    botControlService.deleteBot(b.getIdCore());
                }
            }
        }
    }

    /**
     * 1st Function at Bot Boot
     * Creates Bot, initiates entry for database, and returns its ID
     * @param modus Type: Independent, partial or full server
     * @return
     */
    @RequestMapping(value = "initiate/{id}/{modus}", method = RequestMethod.GET)
    public long initiate(@PathVariable("id") Long id, @PathVariable("modus") String modus){
        Bot bot = new Bot(id);
        bot.setWorkingMode(modus);
        bot.setJobId((long) 0);
        bot.setLinkId(linkControlService.getLink((long) 1));
        bot.setPercentageCompleted(100);
        bot.setIdStart((long) 1);
        bot.setIdStop((long) 1);
        bot.setBusy(false);
        bot.updateStatus(BotState.Alive.ordinal());
        botControlService.saveBot(bot);
        logger.info("Bot with id "+bot.getIdCore()+" entered the network!");
        return bot.getIdCore();
    }
}
