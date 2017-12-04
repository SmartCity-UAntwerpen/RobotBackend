package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.models.*;
import be.uantwerpen.sc.services.BotControlService;
import be.uantwerpen.sc.services.LinkControlService;
import be.uantwerpen.sc.services.TimerService;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
     * Autowired Map Control Service
     */
    @Autowired
    private MapController mapController;

    /**
     * Get All Bots
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<Bot> allBots()
    {
        return botControlService.getAllBots();
    }

    /**
     * BackBone IP (Probably)(TODO)
     */
    @Value("${backbone.ip:default}")
    String coreIp;
    /**
     * BackBone Port (Probably)(TODO)
     */
    @Value("${backbone.port:default}")
    String corePort;

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
        botControlService.updateBot(bot);
    }

    @RequestMapping(value = "updateBotTest/{id}",method = RequestMethod.GET)
    public void updateBotTest(@PathVariable("id") Long id)
    {
        Bot botEntity = new Bot();
        botEntity.setId(id);
        botEntity.setState("Updated");
        botControlService.updateBot(botEntity);
    }

    @RequestMapping(value = "test",method = RequestMethod.GET)
    public Bot testRestBot()
    {
        return new Bot();
    }

    @RequestMapping(value = "savetest",method = RequestMethod.GET)
    public void saveBotTest()
    {
        Bot bot = new Bot();
        bot.setState("test");
        botControlService.saveBot(bot);
    }

    @RequestMapping(value = "goto/{id}/{rid}",method = RequestMethod.GET)
    public String goTo(@PathVariable("id") Long id, @PathVariable("rid") Long rid)
    {
        Bot botEntity = botControlService.getBotWithCoreId(id);
        /*if (!pointEntities.contains(botEntity.getLinkId().getStopId())){
            pointEntities.add(botEntity.getLinkId().getStopId());
        }*/
        if (botEntity != null)
        {
            if (botEntity.getPercentageCompleted() >= 50)
            {
                // stack.push(botEntity.getLinkId().getStopId());
            }
        }
        else
        {
            System.out.println("Robot does not exist");
        }

        return "Something";
    }

    /**
     * Robot calls this GET
     * @return
     */
    @RequestMapping(value = "newRobot", method = RequestMethod.GET)
    public Long newRobot()
    {
        Bot bot = new Bot();
        System.out.println(bot);
        //Save bot in database and get bot new rid
        bot = botControlService.saveBot(bot);

        Date date = new Date();
        System.out.println("New robot created!! - " + date.toString());

        return bot.getIdCore();
    }

    @RequestMapping(value = "{id}/lid/{lid}", method = RequestMethod.GET)
    public void locationLink(@PathVariable("id") Long id, @PathVariable("lid") Long lid)
    {
        Bot bot = botControlService.getBotWithCoreId(id);
        Link link;

        if(bot != null)
        {
            link = linkControlService.getLink(lid);

            if(link != null)
            {
                bot.setLinkId(link);
                botControlService.updateBot(bot);
                System.out.println(bot.getIdCore());
            }
            else
            {
                System.out.println("Link with id: " + lid + " not found!");
            }
        }
        else
        {
            System.out.println("Bot with id:" + id + " not found!");
        }
    }

    public void updateLocation(Long id, Long idvertex, int progress)
    {
        Bot bot = botControlService.getBotWithCoreId(id);

        if(bot != null)
        {
            bot.setLinkId(linkControlService.getLink(idvertex));
            bot.setPercentageCompleted(progress);
            botControlService.saveBot(bot);
        }
    }

    @RequestMapping(value = "delete/{rid}",method = RequestMethod.GET)
    public void deleteBot(@PathVariable("rid") Long rid)
    {
        Bot b = botControlService.getBotWithCoreId(rid);
        System.out.println(b.getId());
        botControlService.deleteBot(b.getId());

        try {
            String u = "http://"+coreIp+":"+corePort+"/bot/delete/"+rid;
            URL url = new URL(u);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        System.out.println("Bot with id: " + rid + " deleted from DB");
    }

    @RequestMapping(value = "/deleteBots}",method = RequestMethod.GET)
    public void resetBots()
    {
        botControlService.deleteBots();
    }

    @RequestMapping(value = "posAll", method = RequestMethod.GET)
    public String posAll(){
        List<Bot> bots = botControlService.getAllBots();
        JSONArray array = new JSONArray();
        for(Bot b : bots){
            Location loc = new Location();
            loc.setVehicleID(b.getIdCore());

            if (b.getBusy()==1){
                loc.setStartID(b.getIdStart());
                loc.setStopID(b.getIdStop());
                loc.setPercentage((long) b.getPercentageCompleted());
            }else if(b.getBusy()==0){
                loc.setStartID(b.getLinkId().getStartPoint().getId());
                loc.setStopID(b.getLinkId().getStartPoint().getId());
                loc.setPercentage( (long)100);
            }

            //opslaan in json file
            JSONObject obj = new JSONObject();
            try{
                obj.put("idVehicle", loc.getVehicleID());
                obj.put("idStart", loc.getStartID());
                obj.put("idEnd", loc.getStopID());
                obj.put("percentage", loc.getPercentage());
            }
            catch (JSONException e) { }
            array.put(obj);
        }
        return array.toString();
    }

    @RequestMapping(value = "checkTimer", method = RequestMethod.GET)
    public void checkTimer(){
        mapController.updateMap();
        List<Bot> bots = botControlService.getAllBots();
        for(Bot b : bots){
            if(b.getAlive()==false){
                deleteBot(b.getIdCore());

            }else{
                b.setAlive(false);
                botControlService.saveBot(b);
            }
        }
    }

    /**
     * 1st Function at Bot Boot
     * Creates Bot, initiates entry for database, and returns its ID
     * @param modus Type: Independent, partial or full server
     * @return
     */
    @RequestMapping(value = "initiate/{modus}", method = RequestMethod.GET)
    public long initiate(@PathVariable("modus") String modus){
        Bot bot = new Bot();
        long id = (long) getNewId();
        bot.setIdCore(id);
        bot.setState(modus);  //werkingsmodus
        bot.setJobId((long) 0);
        bot.setLinkId(linkControlService.getLink((long) 1));
        bot.setPercentageCompleted(100);
        bot.setIdStart((long) 1);
        bot.setIdStop((long) 1);
        bot.setBusy(0);
        bot.setAlive(true);
        //id ook doorgeven naar robot zelf
        System.out.println(bot.getId());
        botControlService.saveBot(bot);
        System.out.println(bot.getId());
        return bot.getIdCore();
    }

    /**
     * Gets New Bot ID for running a job? Something something calcweight
     * TODO: Timeout HTTP
     * @return Bot ID
     */
    public int getNewId(){
        String data = "";
        try {
            URL url = new URL("http://"+coreIp+":"+corePort+"//bot/newBot/robot");
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
        System.out.println(data);
        return Integer.parseInt(data);
    }

    //-------- TESTING --------
    @RequestMapping(value = "alive/{id}", method = RequestMethod.GET)
    public void alive(@PathVariable("id") long id){
        Bot b = botControlService.getBot(id);
        b.setAlive(true);
        botControlService.saveBot(b);
        System.out.println("Bot geeft tegen van leven");
    }
}
