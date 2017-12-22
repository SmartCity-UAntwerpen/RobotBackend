package be.uantwerpen.sc.tools.pathplanning;

import be.uantwerpen.sc.models.Link;
import be.uantwerpen.sc.models.map.Map;
import be.uantwerpen.sc.models.map.Path;
import be.uantwerpen.sc.tools.Vertex;
import org.springframework.security.access.method.P;

import java.util.List;

/**
 * Path Planning interface TODO
 */
public interface IPathplanning
{
    Path CalculatePath(int start, int stop);
    double CalculatePathWeight(int start, int stop);
    Path nextRandomPath(Map map, int start);

}
