package be.uantwerpen.sc.services;

import be.uantwerpen.rc.models.map.Map;
import be.uantwerpen.rc.models.map.Path;
import be.uantwerpen.rc.models.map.Link;
import be.uantwerpen.rc.models.map.Node;
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
    public Path CalculatePathNonInterface(int start, int stop) {
        Map map = mapControlService.getMap();
        List<Link> linkEntityList = new ArrayList<>();
        List<Vertex> vertexes = new ArrayList<>();
        for (Node nj : map.getNodeList()) {
            vertexes.add(new Vertex(nj));
            linkEntityList.addAll(nj.getNeighbours());
        }

        ArrayList<Edge> edges;
        List<ArrayList<Edge>> edgeslistinlist = new ArrayList<>();
        Link realLink = new Link();
        int i = 0;
        for (Node nj : map.getNodeList()) {
            edges = new ArrayList<>();
            for (Link neighbour : nj.getNeighbours()) {
                for (Vertex v : vertexes) {
                    if (Objects.equals(v.getId(), neighbour.getEndPoint().getId())) {
                        for (Link linkEntity : linkEntityList) {
                            if (Objects.equals(linkEntity.getEndPoint().getId(), v.getId()) && Objects.equals(linkEntity.getStartPoint().getId(), nj.getPointEntity().getId())) {
                                System.out.println(linkEntity.toString() + " " + linkEntity);
                                realLink = linkEntity;
                            }
                        }
                        //edges.add(new Edge(v.getId(),neighbour.getWeight(),linkControlService.getLink(neighbour.getPointEntity().getPid())));
                        edges.add(new Edge(v.getId(), neighbour.getWeight(), realLink));
                    }
                }
            }
            edgeslistinlist.add(i, (edges));
            i++;
        }

        for (int j = 0; j < vertexes.size(); j++) {
            vertexes.get(j).setAdjacencies(edgeslistinlist.get(j));
        }

        dijkstra.computePaths((long) start, vertexes); // run Dijkstra
        return dijkstra.getShortestPathTo((long) stop, vertexes);
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
        List<Vertex> vertexes = mapControlService.getVertexMap();
        mapControlService.resetVertex();
        dijkstra.computePaths((long) start, vertexes);
        return dijkstra.getShortestPathTo((long) stop, vertexes);
    }

    @Override
    public double CalculatePathWeight(int start, int stop) {
        Path p = CalculatePath(start, stop);
        return p.getWeight();
    }

    public double CalculatePathLength(int start, int stop) {
        Path p = CalculatePath(start, stop);
        return p.getWeight();
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
        List<Vertex> vertexes = mapControlService.getVertexMap();

        Random random = new Random();
        Vertex currentVertex = null;
        for (Vertex v : vertexes) {
            if (v.getId() == start) {
                currentVertex = v;
            }
        }
        int i = currentVertex.getAdjacencies().size();
        int index = random.nextInt(i);
        Vertex nextVertex = new Vertex(new Node(currentVertex.getAdjacencies().get(index).getLinkEntity().getEndPoint()));

        List<Vertex> vertexList = new ArrayList<>();
        vertexList.add(currentVertex);
        vertexList.add(nextVertex);
        return new Path(vertexList);
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
        List<Vertex> vertices = path.getPath();
        Collections.reverse(vertices);
        List<Link> links = new LinkedList<>();
        for (Vertex v : vertices) {
            if (v.getPrevious() == null) {
                commands.addDriveDir(new DriveDir(DriveDirEnum.FORWARD));
                commands.addDriveDir(new DriveDir(DriveDirEnum.FOLLOW));
                break;
            }
            for (Edge l : v.getPrevious().getAdjacencies()) {
                if (Objects.equals(l.getTarget(), v.getId()))
                    links.add(l.getLinkEntity());
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
