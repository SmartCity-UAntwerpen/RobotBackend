package be.uantwerpen.sc.services;

import be.uantwerpen.sc.models.Link;
import be.uantwerpen.sc.models.LinkNG;
import be.uantwerpen.sc.repositories.LinkNGRepository;
import be.uantwerpen.sc.repositories.LinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Link Control Service
 */
@Service
public class LinkNGControlService
{
    /**
     * Autowired Link Repository
     */
    @Autowired
    private LinkNGRepository linkRepository;

    /**
     * Encapsulator for finding all available links
     * @return List of links
     */
    public List<LinkNG> getAllLinks()
    {
        return linkRepository.findAll();
    }

    /**
     * Encapsulator for finding a link by id
     * @param id ID of link
     * @return Link
     */
    public LinkNG getLink(Long id)
    {
        return linkRepository.findOne(id);
    }

    /**
     * Encapsulator for saving a link
     * @param link link to save
     */
    public void save(LinkNG link)
    {
        linkRepository.save(link);
    }
}
