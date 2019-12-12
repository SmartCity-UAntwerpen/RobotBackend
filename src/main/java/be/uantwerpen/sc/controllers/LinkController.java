package be.uantwerpen.sc.controllers;

import be.uantwerpen.rc.models.Bot;
import be.uantwerpen.rc.models.map.Link;
import be.uantwerpen.sc.services.BotControlService;
import be.uantwerpen.sc.services.LinkControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author  Niels on 30/03/2016.
 * @author Reinout
 * @author Dieter 2018-2019
 *
 * Link Controller
 *
 */
@RestController
@RequestMapping("/link/")
public class LinkController
{
    /**
     * Autowired Link Control Service
     */
    @Autowired
    private LinkControlService linkControlService;

    @Autowired
    private BotControlService botService;

    private Logger logger = LoggerFactory.getLogger(LinkController.class);


    /**
     * Get list of all available
     * @return list of links
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<Link> allLinks()
    {
        return linkControlService.getAllLinks();
    }

    /**
     * Called by bot to lock a link
     * @param id ID of link to lock
     * @return Success
     */
    @RequestMapping(value = "requestlock/{botId}/{id}", method = RequestMethod.GET)
    public boolean requestLinkLock(@PathVariable("botId") Long botId,@PathVariable("id") Long id)
    {
        synchronized(this)
        {
            Link link = linkControlService.getLink(id);

            if(link == null) //Link not found
            {
                return false;
            }
            try{
                if(link.getLock().getStatus() && !link.getLock().getLockedBy().getIdCore().equals(botId)) {
                    logger.info("Bot "+botId+" was denied lock for link: "+id);
                    return false;
                } else{
                    //Point not locked -> attempt lock
                    Bot bot = botService.getBot(botId);
                    link.lockLink(true,bot);
                    link.getCost().setWeight(link.getCost().getWeight()+10);
                    linkControlService.save(link);
                    logger.info("Bot "+botId+" locked link: "+link.getId());
                    return true;
                }
            } catch(NullPointerException e){
                logger.error("Error locking link: "+id);
                return false;
            }

        }

    }

    /**
     * Called by bot to unlock a link
     * @param id ID of link to lock
     * @return Success
     */
    @RequestMapping(value = "unlock/{botId}/{id}", method = RequestMethod.GET)
    public boolean LinkUnLock(@PathVariable("botId") Long botId, @PathVariable("id") Long id)
    {
        synchronized(this)
        {
            Link link = linkControlService.getLink(id);

            if(link == null)//Link not found
            {
                return false;
            }

            try{
                //Check if bot asking the unlock is the one that locked the link
                if(link.getLock().getLockedBy().getIdCore().equals(botId) && link.getLock().getStatus()) {
                    //Point already locked
                    link.lockLink(false, null);
                    link.getCost().setWeight(1);
                    linkControlService.save(link);
                    logger.info("Bot "+botId+" unlocked link: "+id);
                return true;
                } else {
                    return false;
                }
            } catch(NullPointerException e){
                logger.error("Bot "+botId+" not found for unlocking link: "+id);
                return false;
            }
        }
    }
}

