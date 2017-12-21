package be.uantwerpen.sc.controllers;

import be.uantwerpen.sc.models.Link;
import be.uantwerpen.sc.services.LinkControlService;
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
        List<Link> linkEntityList = linkControlService.getAllLinks();
        return linkEntityList;
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
            switch(link.getLocked())
            {
                case 1: //Point already locked
                    return false;
                case 0: //Point not locked -> attempt lock
                    link.setLocked(1);
                    link.setTrafficWeight(link.getTrafficWeight()+10);
                    linkControlService.save(link);
                    return true;
            }
        }

        return false;
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

            switch(link.getLocked())
            {
                case 1: //Point already locked
                    link.setLocked(0);
                    link.setTrafficWeight(link.getTrafficWeight()-10);
                    linkControlService.save(link);
                    return true;
                case 0: //Point not locked -> attempt lock
                    return false;
            }
        }
        return false;
    }
}

