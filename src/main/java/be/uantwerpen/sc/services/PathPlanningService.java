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
     * Calculates Path
     *
     * @param start, start point
     * @param stop,  stop point
     * @return shortest path
     */
    public Path CalculatePathNonInterface(long start, long stop) {
        Map map = mapControlService.getMap();
        /*List<Link> linkEntityList = new ArrayList<>();
        List<Point> vertexes = new ArrayList<>();
        for (Point node : map.getPointList()) {
            vertexes.add(node);
            linkEntityList.addAll(node.getNeighbours());
        }

        ArrayList<Link> links;
        List<ArrayList<Link>> linksListInList = new ArrayList<>();
        Link realLink = new Link();
        int i = 0;
        for (Point node : map.getPointList()) {
            links = new ArrayList<>();
            for (Link neighbour : node.getNeighbours()) {
                for (Point v : vertexes) {
                    if (Objects.equals(v.getId(), neighbour.getEndPoint())) {
                        for (Link linkEntity : linkEntityList) {
                            if (Objects.equals(linkEntity.getEndPoint(), v.getId()) && Objects.equals(linkEntity.getStartPoint(), node.getId())) {
                                System.out.println(linkEntity.toString() + " " + linkEntity);
                                realLink = linkEntity;
                            }
                        }
                        //edges.add(new Edge(v.getId(),neighbour.getWeight(),linkControlService.getLink(neighbour.getPointEntity().getPid())));
                        links.add(new Link(v.getId(), neighbour.getCost().getWeight()));
                    }
                }
            }
            linksListInList.add(i, (links));
            i++;
        }

        for (int j = 0; j < vertexes.size(); j++) {
            vertexes.get(j).setNeighbours(linksListInList.get(j));
        }*/

        dijkstra.computePaths(start, map.getPointList()); // run Dijkstra
        return dijkstra.getShortestPathTo(stop, map.getPointList());
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
        List<Point> vertexes = mapControlService.getVertexMap();
        mapControlService.resetVertex();
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
        // TODO: transitions from map-classes to pathplanning-classes


        /*List<Link> linkEntityList = new ArrayList<>();
        List<Point> vertexes = new ArrayList<>();
        for (Point node : map.getPointList()){
            System.out.print(node);
            vertexes.add(node);
            linkEntityList.addAll(node.getNeighbours());
        }

        ArrayList<Link> edges = new ArrayList<>();
        List<ArrayList<Link>> edgesListInList = new ArrayList<>();
        Link realLink = new Link();
        int i = 0;
        for (Point node : map.getPointList())
        {
            edges.clear();
            for (Link neighbour : node.getNeighbours())
            {
                for (Point v : vertexes)
                {
                    if(v.getId().equals(neighbour.getEndPoint()))
                    {
                        for(Link linkEntity: linkEntityList)
                        {
                            if(linkEntity.getEndPoint().equals(v.getId()) && linkEntity.getStartPoint().equals(node.getId()))
                            {
                                realLink = linkEntity;
                            }
                        }
                        edges.add(new Link(v.getId(), neighbour.getCost().getWeight()));
                    }
                }
            }
            edgesListInList.add(i, (edges));
            i++;
        }

        for (int j = 0; j < vertexes.size();j++){
            vertexes.get(j).setNeighbours(edgesListInList.get(j));
        }*/


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
