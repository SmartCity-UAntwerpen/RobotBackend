package be.uantwerpen.sc.services;

import be.uantwerpen.sc.controllers.BotController;
import be.uantwerpen.rc.models.Bot;
import be.uantwerpen.sc.repositories.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Bot Control Service
 */
@Service
public class BotControlService
{
    /**
     * Autowired Bot repository
     */
    @Autowired
    private BotRepository botRepository;

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
     * Encapsulator to save Bot to DB
     * @param bot Bot to save
     */
    public void saveBot(Bot bot)
    {
        botRepository.save(bot);
    }

    /**
     * Encapsulator to get Bot by ID
     * @param id ID of Bot to get
     * @return Bot
     */
    public Bot getBot(Long id)
    {
        return botRepository.findOne(id);
    }

    /**
     * Encapsulator to get all available bots from DB
     * @return List of Bots
     */
    public List<Bot> getAllBots()
    {
        return botRepository.findAll();
    }

    /**
     * Deletes Bot with specified ID
     * @param bid ID of bot to remove
     * @return Success
     */
    public boolean deleteBot(long bid)
    {
        if(this.getBot(bid) == null)
            return false;
        botRepository.delete(bid);
        try {
            String u = "http://"+backboneIP+":"+backbonePort+"/bot/delete/"+bid;
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
        return true;
    }

    /**
     * Deletes all available bots
     * @return
     */
    public void deleteBots()
    {
        botRepository.deleteAll();
    }
}
