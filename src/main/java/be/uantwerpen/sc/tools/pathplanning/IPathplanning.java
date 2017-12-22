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
    List<Vertex> CalculatePath(int start, int stop);
    double CalculatePathWeight(int start, int stop);
    List<Vertex> nextRandomPath(Map map, int start);

}
