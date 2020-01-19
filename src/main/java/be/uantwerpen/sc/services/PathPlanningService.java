package be.uantwerpen.sc.services;

import be.uantwerpen.rc.models.map.Map;
import be.uantwerpen.rc.models.map.Path;
import be.uantwerpen.rc.models.map.Link;
import be.uantwerpen.rc.models.map.Point;
import be.uantwerpen.rc.tools.*;
import be.uantwerpen.rc.tools.pathplanning.Dijkstra;
import be.uantwerpen.rc.tools.pathplanning.IPathplanning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service for path planning
 * TODO: implement other path planning algorithms and other navigations modes for the bots
 */
@Service
public class PathPlanningService implements IPathplanning {

    /**
     * Autowired map control service
     */
    @Autowired
    private MapControlService mapControlService;

    /**
     * Dijkstra Path Finder
     * Todo Why hardcoded?
     */
    private Dijkstra dijkstra;

    /**
     * Creates path planning service
     * TODO: hardcoded dijkstra
     */
    public PathPlanningService() {
        this.dijkstra = new Dijkstra();
    }


    /**
     * Calculates path following Dijkstra
     * TODO Hardcoded Dijkstra
     *
     * @param start start int (?)
     * @param stop  stop int (?)
     * @return List of Vertexes following the shortest path
     */
    @Override
    public Path CalculatePath(int start, int stop) {
        List<Point> vertexes = mapControlService.getMap().getPointList();
        //mapControlService.resetMap();
        dijkstra.computePaths((long) start, vertexes);
        return dijkstra.getShortestPathTo((long) stop, vertexes);
    }

    @Override
    public double CalculatePathWeight(int start, int stop) {
        Path p = this.CalculatePath(start, stop);
        return p.getWeight();
    }

    public double CalculatePathLength(int start, int stop) {
        Path p = this.CalculatePath(start, stop);
        return p.getWeight();
    }

    public List<Point> Calculatepath(Map map, long start, long stop) {
        // TODO: should be completely independent of Dijkstra
        dijkstra.computePaths(start, map.getPointList()); // run Dijkstra
        List<Point> path = dijkstra.getShortestPathTo(stop,map.getPointList()).getPath();
        System.out.println("Path: " + path);
        return path;
    }

















    /**
     * Gets a random next vertex from the start vertex and returns this as path
     *
     * @param map
     * @param start
     * @return
     */
    @Deprecated
    @Override
    public Path nextRandomPath(Map map, int start) {
/*        List<Point> vertexes = mapControlService.getVertexMap();

        Random random = new Random();
        Point currentVertex = null;
        for (Point v : vertexes) {
            if (v.getId() == start) {
                currentVertex = v;
            }
        }
        int i = currentVertex.getNeighbours().size();
        int index = random.nextInt(i);
        Point nextVertex = currentVertex.getNeighbours().get(index).getEndPoint();

        List<Point> vertexList = new ArrayList<>();
        vertexList.add(currentVertex);
        vertexList.add(nextVertex);
        return new Path(vertexList);*/
        return null;
    }



    /**
     * Method used in old full server navigation
     * TODO update
     *
     * @param path
     * @return
     */
    @Deprecated
    public DriveDirEncapsulator createBotDriveDirs(Path path) {
        DriveDirEncapsulator commands = new DriveDirEncapsulator();
        List<Point> vertices = path.getPath();
        Collections.reverse(vertices);
        List<Link> links = new LinkedList<>();
        for (Point v : vertices) {
            if (v.getPrevious() == null) {
                commands.addDriveDir(new DriveDir(DriveDirEnum.FORWARD));
                commands.addDriveDir(new DriveDir(DriveDirEnum.FOLLOW));
                break;
            }
            for (Link l : v.getPrevious().getNeighbours()) {
                if (Objects.equals(l.getTarget(), v.getId()))
                    links.add(l);
            }
        }
        Collections.reverse(links);
        Link previous = null;
        for (Link l : links) {
            if (previous == null) {
                previous = l;
                continue;
            }
            if (l.getAngle() == -1.0) { //TODO: change follow command condition
                commands.addDriveDir(new DriveDir(DriveDirEnum.FOLLOW));
            } else if (l.getAngle() == 0) {
                commands.addDriveDir(new DriveDir(DriveDirEnum.FORWARD));
            } else if (l.getAngle() < 0) {
                DriveDir d = new DriveDir(DriveDirEnum.LEFT);
                d.setAngle(-l.getAngle());
                commands.addDriveDir(d);
            } else if (l.getAngle() == 180) {
                commands.addDriveDir(new DriveDir(DriveDirEnum.TURN));
            } else {
                DriveDir d = new DriveDir(DriveDirEnum.RIGHT);
                d.setAngle(l.getAngle());
                commands.addDriveDir(d);
            }

            //commands.addDriveDir(new DriveDir(DriveDirEnum.FOLLOW));
            previous = l;
        }
        return commands;
    }
}
