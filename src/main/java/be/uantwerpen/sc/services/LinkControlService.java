package be.uantwerpen.sc.services;

import be.uantwerpen.sc.models.Link;
import be.uantwerpen.sc.repositories.LinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Link Control Service
 */
@Service
public class LinkControlService
{
    /**
     * Autowired Link Repository
     */
    @Autowired
    private LinkRepository linkRepository;

    /**
     * Encapsulator for finding all available links
     * @return List of links
     */
    public List<Link> getAllLinks()
    {
        return linkRepository.findAll();
    }

    /**
     * Encapsulator for finding a link by id
     * @param id ID of link
     * @return Link
     */
    public Link getLink(Long id)
    {
        return linkRepository.findOne(id);
    }

    /**
     * Encapsulator for saving a link
     * @param link link to save
     */
    public void save(Link link)
    {
        linkRepository.save(link);
    }
}
