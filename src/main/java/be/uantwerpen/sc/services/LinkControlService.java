package be.uantwerpen.sc.services;

import be.uantwerpen.rc.models.Bot;
import be.uantwerpen.rc.models.map.Link;
import be.uantwerpen.rc.models.map.LinkLock;
import be.uantwerpen.sc.repositories.LinkLockRepository;
import be.uantwerpen.sc.repositories.LinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Dieter 2018-2019
 * <p>
 * LinkControlService
 */
@Service
public class LinkControlService {

    @Autowired
    private LinkRepository links;

    @Autowired
    private LinkLockRepository locks;

    /**
     * Encapsulator for finding all available links
     *
     * @return List of links
     */
    public List<Link> getAllLinks() {
        return links.findAll();
    }

    /**
     * Encapsulator for finding a link by id
     *
     * @param id ID of link
     * @return Link
     */
    public Link getLink(Long id) {
        return links.findOne(id);
    }

    /**
     * Encapsulator for saving a link
     *
     * @param link link to save
     */
    public void save(Link link) {
        links.save(link);
    }

    /**
     * Lock the tile of which a point belongs to
     *
     * @param id,     point id
     * @param status, lock status
     */
    public void setLock(Long id, Boolean status, Bot bot) {
        this.getLink(id).lockLink(status, bot);
    }

    /**
     * Returns the lock status of a Tile of which a point belongs to
     *
     * @param id, point id
     * @return lock status
     */
    public Boolean getLock(Long id) {
        return this.getLink(id).getLockStatus();
    }

    /**
     * Releases all link locks
     */
    public void removeAllLocks(){
        List<LinkLock> lockList = locks.findAll();
        for(LinkLock l:lockList){
            l.setStatus(false);
            l.setLockedBy(null);
            locks.save(l);
        }
    }


    /**
     * Remove all links locks from a specific bot
     *
     * @param bot, the bot
     */
    public void removeAllLocksFromBot(Bot bot) {
        List<LinkLock> l = locks.findAllBylockedBy(bot);
        for (LinkLock lo : l) {
            lo.setStatus(false);
            lo.setLockedBy(null);
            locks.save(lo);
        }
    }
}
