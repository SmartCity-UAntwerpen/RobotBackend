package be.uantwerpen.sc.tools.pathplanning;

import be.uantwerpen.sc.models.Link;
import be.uantwerpen.sc.models.map.Map;
import be.uantwerpen.sc.tools.Vertex;

import java.util.List;

/**
 * Path Planning interface TODO
 */
public interface IPathplanning
{
    List<Vertex> Calculatepath(int start, int stop, List<Link> links);
    List<Vertex> nextRandomPath(Map map, int start, List<Link> links);

}
