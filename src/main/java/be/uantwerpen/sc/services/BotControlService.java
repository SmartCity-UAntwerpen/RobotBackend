package be.uantwerpen.sc.services;

import be.uantwerpen.rc.models.Bot;
import be.uantwerpen.sc.repositories.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Dieter 2018-2019
 * <p>
 * Bot Control Service
 */
@Service
public class BotControlService {
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
     *
     * @param bot Bot to save
     */
    public void saveBot(Bot bot) {
        botRepository.save(bot);
    }

    /**
     * Encapsulator to get Bot by ID
     *
     * @param id ID of Bot to get
     * @return Bot
     */
    public Bot getBot(Long id) {
        return botRepository.findOne(id);
    }

    /**
     * Encapsulator to get all available bots from DB
     *
     * @return List of Bots
     */
    public List<Bot> getAllBots() {
        return botRepository.findAll();
    }

    /**
     * Deletes Bot with specified ID
     *
     * @param bid ID of bot to remove
     * @return Success
     */
    public boolean deleteBot(long bid) {
        if (this.getBot(bid) == null)
            return false;
        botRepository.delete(bid);
        return true;
    }

    /**
     * Returns a List of all not busy bots
     *
     * @return List of Bots
     */
    public List<Bot> getAllAvailableBots() {
        return botRepository.findAllByBusyFalseAndPointNotNull();
    }
}
