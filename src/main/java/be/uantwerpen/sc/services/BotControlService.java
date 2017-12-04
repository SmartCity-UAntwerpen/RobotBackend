package be.uantwerpen.sc.services;

import be.uantwerpen.sc.controllers.BotController;
import be.uantwerpen.sc.models.Bot;
import be.uantwerpen.sc.repositories.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     * Autowired Bot controller
     */
    @Autowired
    private BotController botController;

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
     * TODO: Remove, id is unneccesary
     * @param id ID of Bot to get
     * @return Bot
     */
    public Bot getBot(Long id)
    {
        return botRepository.findOne(id);
    }

    /**
     * Gets Bot with core ID
     * TODO: fix repository findall to work with coreid
     * @param idCore Core ID of bot to find
     * @return Bot
     */
    public Bot getBotWithCoreId(Long idCore){
        List<Bot> bots = botRepository.findAll();
        for (Bot bot:bots){
            if(bot.getIdCore().equals(idCore)){
                return bot;
            }
        }
        return null;
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
     * @param rid ID of bot to remove
     * @return Success
     */
    public boolean deleteBot(long bid)
    {
        if(this.getBot(bid) == null)
            return false;
        botRepository.delete(bid);
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
