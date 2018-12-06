package be.uantwerpen.sc.tools.pathplanning;

import be.uantwerpen.sc.models.map.newMap.Map;
import be.uantwerpen.sc.models.map.Path;

/**
 * Path Planning interface TODO
 */
public interface IPathplanning
{
    Path CalculatePath(int start, int stop);
    double CalculatePathWeight(int start, int stop);
    Path nextRandomPath(Map map, int start);

}
