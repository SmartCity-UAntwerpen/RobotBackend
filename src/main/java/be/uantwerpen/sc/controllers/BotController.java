package be.uantwerpen.sc.controllers;


import be.uantwerpen.rc.models.Bot;
import be.uantwerpen.rc.models.BotState;
import be.uantwerpen.rc.models.Job;
import be.uantwerpen.rc.models.Location;
import be.uantwerpen.rc.models.map.Point;
import be.uantwerpen.sc.services.BotControlService;
import be.uantwerpen.sc.services.JobControlService;
import be.uantwerpen.sc.services.LinkControlService;
import be.uantwerpen.sc.services.PointControlService;
import be.uantwerpen.sc.services.TileControlService;
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
 * @author Dieter 2018-2019
 * Bot Controller
 */
@RestController
@RequestMapping("/bot/")
public class BotController {
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
     * Autowired Tile Control Service
     */
    @Autowired
    private TileControlService tileControlService;

    /**
     * Autowired Job Service
     */
    @Autowired
    private JobControlService jobControlService;

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

    private Logger logger = LoggerFactory.getLogger(BotController.class);


    /**
     * Get All Bots
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<Bot> allBots() {
        return botControlService.getAllBots();
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public Bot getBot(@PathVariable("id") Long id) {
        return botControlService.getBot(id);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.POST)
    public void saveBot(@PathVariable("id") Long id, @RequestBody Bot bot) {
        botControlService.saveBot(bot);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public void updateBot(@PathVariable("id") Long id, @RequestBody Bot bot) {
        botControlService.saveBot(bot);
    }

    /**
     * Robot calls this
     *
     * @return id
     */
    @Deprecated
    @RequestMapping(value = "newRobot/{id}", method = RequestMethod.GET)
    public Long newRobot(@PathVariable("id") Long id) {
        Bot bot = new Bot(id);
        System.out.println(bot);
        //Save bot in database and get bot new rid
        botControlService.saveBot(bot);

        Date date = new Date();
        logger.info("New robot created!! - " + date.toString());

        return bot.getIdCore();
    }

    /**
     * Bot location update
     *
     * @param id,  the bot id
     * @param pid, the location = a point id
     */
    @RequestMapping(value = "{id}/locationUpdate/{pid}", method = RequestMethod.GET)
    public void locationLink(@PathVariable("id") Long id, @PathVariable("pid") Long pid) {
        Bot bot = botControlService.getBot(id);
        Point point;

        if (bot != null) {
            point = pointControlService.getPoint(pid);
            logger.info("Bot with id: " + id + " updated its location: " + pid);
            if (point != null) {
                bot.setPoint(pid);
                botControlService.saveBot(bot);
                System.out.println(bot.getIdCore());
            } else
                System.out.println("Point with id: " + pid + " not found!");
        } else
            System.out.println("Bot with id:" + id + " not found!");
    }

    /**
     * Updated a bots location
     *
     * @param id        the bot id
     * @param pointId,  point location
     * @param progress, the bots progress on the link
     */
    public void updateLocation(Long id, Long pointId, int progress) {
        Bot bot = botControlService.getBot(id);

        if (bot != null) {
            bot.setPoint(pointId);
            bot.setPercentageCompleted(progress);
            bot.updateStatus(BotState.Alive.ordinal());
            botControlService.saveBot(bot);
        }
    }

    /**
     * Delete a bot from database
     * First all the references to the bot are removed (foreign key constraints)
     *
     * @param rid, bot id
     */
    @RequestMapping(value = "delete/{rid}", method = RequestMethod.GET)
    public void deleteBot(@PathVariable("rid") Long rid) {
        logger.info("Removing bot with id: " + rid);
        Bot bot = botControlService.getBot(rid);
        //Remove all references to the bot
        linkControlService.removeAllLocksFromBot(bot);
        tileControlService.removeAllLocksFromBot(bot);

        //Remove the job the bot was executing
        List<Job> jobs = jobControlService.getExecutingJob(bot);
        for (Job j : jobs) {
            //Remove the bot from executing it and set starting point to the bots last location
            //j.setIdStart(bot.getPoint());
            j.setBot(null);
            jobControlService.saveJob(j);
            //Also queue job again so it will be executed again
            jobControlService.queueJob(j.getJobId(), j.getIdStart(), j.getIdEnd());
        }

        //Remove the bot itself
        try {
            botControlService.deleteBot(rid);
            logger.info("Bot " + rid + " has been removed!");
        } catch (Exception e) {
            logger.error("Bot with ID: " + rid + "could not be deleted!");
        }

    }

    /**
     * Get all bot positions
     *
     * @return
     */
    @RequestMapping(value = "posAll", method = RequestMethod.GET)
    public String posAll() {
        List<Bot> bots = botControlService.getAllBots();
        JSONArray array = new JSONArray();
        for (Bot b : bots) {
            Location loc = new Location();
            loc.setVehicleID(b.getIdCore());

            if (b.getBusy()) {
                loc.setStartID(b.getIdStart());
                loc.setStopID(b.getIdStop());
                loc.setPercentage((long) b.getPercentageCompleted());
            } else {
                loc.setStartID(b.getLinkId());
                loc.setStopID(b.getLinkId());
                loc.setPercentage((long) 100);
            }

            JSONObject obj = new JSONObject();
            try {
                obj.put("idVehicle", loc.getVehicleID());
                obj.put("idStart", loc.getStartID());
                obj.put("idEnd", loc.getStopID());
                obj.put("percentage", loc.getPercentage());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(obj);
        }
        return array.toString();
    }


    /**
     * Check if the bots are alive
     * if not remove them
     */
    @RequestMapping(value = "checkTimer", method = RequestMethod.GET)
    public void checkTimer() {
        logger.info("Checking Alive Bots");
        List<Bot> bots = botControlService.getAllBots();
        long currentDate = new Date().getTime();
        for (Bot b : bots) {
            if (b.getStatus() != BotState.Unknown.ordinal()) {
                if (currentDate - b.getLastUpdated().getTime() > 1000 * 60 * 5) {
                    b.updateStatus(BotState.Unknown.ordinal());
                    botControlService.saveBot(b);
                }
            } else {
                if (new Date().getTime() - b.getLastUpdated().getTime() > 1000 * 60 * 5) {
                    this.deleteBot(b.getIdCore());
                }
            }
        }
    }

    /**
     * 1st Function at Bot Boot
     * Creates Bot, initiates entry for database, and returns its ID
     *
     * @param modus Type: Independent, partial or full server (Currently only independent implemented)
     * @return the id
     */
    @RequestMapping(value = "initiate/{id}/{modus}", method = RequestMethod.GET)
    public long initiate(@PathVariable("id") Long id, @PathVariable("modus") String modus) {
        Bot bot = new Bot(id);
        bot.setWorkingMode(modus);
        bot.setJobId((long) 0);
        bot.setLinkId(1L); //Set location to link 1
        bot.setPercentageCompleted(100);
        bot.setIdStart((long) 1);
        bot.setIdStop((long) 1);
        bot.setBusy(false);
        bot.updateStatus(BotState.Alive.ordinal());
        botControlService.saveBot(bot);
        logger.info("Bot with id " + bot.getIdCore() + " entered the network!");
        return bot.getIdCore();
    }
}
