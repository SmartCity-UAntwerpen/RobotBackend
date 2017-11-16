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
     * TODO useful?
     * @param bot Bot to save
     * @return Bot TODO Useful?
     */
    public Bot saveBot(Bot bot)
    {
        return botRepository.save(bot);
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
     * Gets Bot with core ID
     * TODO: What the fuck is a core ID
     * @param idCore Core ID of bot to find
     * @return Bot
     */
    public Bot getBotWithCoreId(Long idCore){
        List<Bot> bots = botRepository.findAll();
        long finalId = 0;
        for (Bot b:bots){
            long idcore = b.getIdCore();
            if(idcore==idCore){
                finalId = b.getId();
            }
        }
        return botRepository.findOne(finalId);
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
     * Updates specified bot info into DB
     * TODO why comment there?
     * @param bot Bot to update
     */
    public void updateBot(Bot bot)
    {
        Bot dbBot = botRepository.findOne(bot.getId());
        dbBot = bot;
        //dbBot.setLinkId(bot.getLinkId());
        botRepository.save(dbBot);
    }

    /**
     * Deletes Bot with specified ID
     * @param rid ID of bot to remove
     * @return Success
     */
    public boolean deleteBot(long bid)
    {
        if(this.getBot(bid) == null)
        {
            //Could not find bot with rid
            return false;
        }
        else
        {
            botRepository.delete(bid);
            return true;
        }
    }

    /**
     * Deletes all available bots
     * TODO Not the same as an actual reset, rename?
     * TODO Pointless return
     * @return
     */
    public boolean resetBots()
    {
        botRepository.deleteAll();
        return true;
    }
}
