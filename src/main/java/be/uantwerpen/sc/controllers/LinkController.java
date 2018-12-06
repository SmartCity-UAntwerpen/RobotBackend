package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.models.map.newMap.Link;
import be.uantwerpen.sc.services.newMap.LinkControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author  Niels on 30/03/2016.
 * @author Reinout
 * HTTP INTERFACE
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

    /**
     * Get <- TODO
     * Get list of all available
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<Link> allBots()
    {
        return linkControlService.getAllLinks();
    }

    /**
     * Called by bot to lock a link
     * @param id ID of link to lock
     * @return Success
     */
    @RequestMapping(value = "requestlock/{id}", method = RequestMethod.GET)
    public boolean requestLinkLock(@PathVariable("id") Long id)
    {
        synchronized(this)
        {
            Link link = linkControlService.getLink(id);

            if(link == null) //Link not found
            {
                return false;
            }
            if(link.getLock().getStatus()) {
                return false;
            } else{
                //Point not locked -> attempt lock
                link.lockLink(true,null);
                link.setWeight(link.getWeight()+10);
                linkControlService.save(link);
                return true;

            }
        }

    }

    /**
     * Called by bot to unlock a link
     * @param id ID of link to lock
     * @return Success
     */
    @RequestMapping(value = "unlock/{id}", method = RequestMethod.GET)
    public boolean LinkUnLock(@PathVariable("id") Long id)
    {
        synchronized(this)
        {
            Link link = linkControlService.getLink(id);

            if(link == null)//Link not found
            {
                return false;
            }

            if(link.getLock().getStatus()) {
                //Point already locked
                link.lockLink(false, null);
                link.setWeight(link.getWeight() - 10);
                linkControlService.save(link);
                return true;
            } else {
                //Point not locked -> attempt lock
                return false;
            }
        }

    }
}

