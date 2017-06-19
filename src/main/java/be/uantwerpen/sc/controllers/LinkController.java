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
 * Created by Niels on 30/03/2016.
 */
@RestController
@RequestMapping("/link/")
public class LinkController
{
    @Autowired
    private LinkControlService linkControlService;

    @RequestMapping(method = RequestMethod.GET)
    public List<Link> allBots()
    {
        List<Link> linkEntityList = linkControlService.getAllLinks();
        return linkEntityList;
    }

    @RequestMapping(value = "requestlock/{id}", method = RequestMethod.GET)
    public boolean requestLinkLock(@PathVariable("id") Long id)
    {
        synchronized(this)
        {
            Link link = linkControlService.getLink(id);

            if(link == null)
            {
                //Point not found
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
                default:
                    break;
            }
        }

        return false;
    }

    @RequestMapping(value = "Unlock/{id}", method = RequestMethod.GET)
    public boolean LinkUnLock(@PathVariable("id") Long id)
    {
        synchronized(this)
        {
            Link link = linkControlService.getLink(id);

            if(link == null)
            {
                //Point not found
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
                default:
                    break;
            }
        }

        return false;
    }
}

