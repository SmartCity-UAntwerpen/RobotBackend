package be.uantwerpen.sc.services;

import be.uantwerpen.sc.models.Link;
import be.uantwerpen.sc.models.map.*;
import be.uantwerpen.sc.models.map.Map;
import be.uantwerpen.sc.tools.*;
import be.uantwerpen.sc.tools.pathplanning.Dijkstra;
import be.uantwerpen.sc.tools.pathplanning.IPathplanning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service for path planning
 * TODO: Shitty class, remake
 */
@Service
public class PathPlanningService implements IPathplanning
{

    /**
     * Autowired map control service
     */
    @Autowired
    private MapControlService mapControlService;

    /**
     * Dijkstra Path Finder
     * Todo Why hard coded?
     */
    private Dijkstra dijkstra;

    /**
     * Creates path planning service
     * TODO: hard coded dijkstra
     */
    public PathPlanningService()
    {
        this.dijkstra = new Dijkstra();
    }

    /**
     * Calculates Path
     * TODO Not Sure
     * @param start
     * @param stop
     * @return
     */
    public Path CalculatePathNonInterface(int start,int stop){
        Map map = mapControlService.getMap();
        List<Link> linkEntityList = new ArrayList<>();
        List<Vertex> vertexes = new ArrayList<>();
        for (Node nj : map.getNodeList()){
            vertexes.add(new Vertex(nj));
            linkEntityList.addAll(nj.getNeighbours());
        }

        ArrayList<Edge> edges;
        List<ArrayList<Edge>> edgeslistinlist = new ArrayList<>();
        Link realLink = new Link();
        int i = 0;
        for (Node nj : map.getNodeList()){
            edges = new ArrayList<>();
            for (Link neighbour : nj.getNeighbours()){
                for (Vertex v : vertexes){
                    if(Objects.equals(v.getId(), neighbour.getStopPoint().getId())){
                        for(Link linkEntity: linkEntityList){
                            if(Objects.equals(linkEntity.getStopPoint().getId(), v.getId()) && Objects.equals(linkEntity.getStartPoint().getId(), nj.getPointEntity().getId())){
                                System.out.println(linkEntity.toString() +" " + linkEntity);
                                realLink = linkEntity;
                            }
                        }
                        //edges.add(new Edge(v.getId(),neighbour.getWeight(),linkControlService.getLink(neighbour.getPointEntity().getPid())));
                        edges.add(new Edge(v.getId(),neighbour.getWeight(),realLink));
                    }
                }
            }
            edgeslistinlist.add(i, (edges));
            i++;
        }

        for (int j = 0; j < vertexes.size();j++){
            vertexes.get(j).setAdjacencies(edgeslistinlist.get(j));
        }


        dijkstra.computePaths(start,vertexes); // run Dijkstra
        return dijkstra.getShortestPathTo(stop,vertexes);
    }

    /**
     * Calculates path following Dijkstra
     * TODO Hardcoded Dijkstra
     * @param start start int (?)
     * @param stop stop int (?)
     * @return List of Vertexes following the shortest path
     */
    @Override
    public Path CalculatePath(int start, int stop) {
        List<Vertex> vertexes = mapControlService.getVertexMap();
        mapControlService.resetVertex();
        dijkstra.computePaths(start,vertexes);
        return dijkstra.getShortestPathTo(stop,vertexes);
    }

    @Override
    public double CalculatePathWeight(int start, int stop){
        Path p=CalculatePath(start,stop);
        return p.getWeight();
    }

    /**
     * Gets a random next vertex from the start vertex and returns this as path
     * @param map
     * @param start
     * @return
     */
    @Override
    public Path nextRandomPath(Map map, int start) {
        List<Vertex> vertexes = mapControlService.getVertexMap();


        Random random = new Random();
        Vertex currentVertex = null;
        for(Vertex v: vertexes){
            if(v.getId()==start) {
                currentVertex = v;
            }
        }
        int i = currentVertex.getAdjacencies().size();
        int index = random.nextInt(i);
        Vertex nextVertex = new Vertex(new Node(currentVertex.getAdjacencies().get(index).getLinkEntity().getStopPoint()));

        List<Vertex> vertexList = new ArrayList<>();
        vertexList.add(currentVertex);
        vertexList.add(nextVertex);
        return new Path(vertexList);
    }

    public List<DriveDir> createBotDriveDirs(Path path){
        List<DriveDir> commands=new ArrayList<>();
        //First part is always driving forward.
        commands.add(DriveDir.FOLLOW);
        List<Vertex> vertices=path.getPath();
        Collections.reverse(vertices);
        List<Link> links=new LinkedList<>();
        for (Vertex v: vertices) {
            if(v.getPrevious()==null)
                break;
            for(Edge l:v.getPrevious().getAdjacencies()) {
                if(Objects.equals(l.getTarget(), v.getId()))
                    links.add(l.getLinkEntity());
            }
        }
        Collections.reverse(links);
        Link previous=null;
        for(Link l: links) {
            if(previous==null) {
                previous=l;
                continue;
            }
            Direction stop=getDirection(l.getStartDirection());
            Direction start=rotate(getDirection(previous.getStopDirection()));
            DriveDir relDir=getNextRelDir(start,stop);
            commands.add(relDir);
            commands.add(DriveDir.FOLLOW);
            previous=l;
        }
        return commands;
    }
    private Direction getDirection(String s){
        switch (s){
            case "S":
                return Direction.SOUTH;
            case "N":
                return Direction.NORTH;
            case "W":
                return Direction.WEST;
            case "E":
                return Direction.EAST;
        }
        return null;
    }

    private Direction rotate(Direction direction){
        switch (direction){
            case NORTH:
                return Direction.SOUTH;
            case SOUTH:
                return Direction.NORTH;
            case EAST:
                return Direction.WEST;
            case WEST:
                return Direction.EAST;
        }
        return null;
    }
    private DriveDir getNextRelDir(Direction startDir, Direction stopDir){
        //Calculate relative direction
        switch(startDir)
        {
            //From NORTH
            case NORTH:
                switch(stopDir)
                {
                    //Go EAST
                    case EAST:
                        return DriveDir.RIGHT;//LEFT);   //Turn LEFT
                    //Go SOUTH
                    case NORTH://SOUTH:
                        return DriveDir.FORWARD;   //Go STRAIGHT
                    //Go WEST
                    case WEST:
                        return DriveDir.LEFT;//RIGHT);   //Turn RIGHT
                    //turn
                    case SOUTH:
                        return DriveDir.TURN;

                }

                //From EAST
            case EAST:
                switch(stopDir)
                {
                    //Go NORTH
                    case NORTH:
                        return DriveDir.LEFT;//RIGHT);   //Turn RIGHT
                    //Go SOUTH
                    case SOUTH:
                        return DriveDir.RIGHT;//LEFT);   //Turn LEFT
                    //Go WEST
                    case EAST://WEST:
                        return DriveDir.FORWARD;   //Go STRAIGHT
                    //turn
                    case WEST:
                        return DriveDir.TURN;
                }

                //From SOUTH
            case SOUTH:
                switch(stopDir)
                {
                    //Go NORTH
                    case SOUTH://NORTH:
                        return DriveDir.FORWARD;   //Go STRAIGHT
                    //Go EAST
                    case EAST:
                        return DriveDir.LEFT;//RIGHT);   //Turn RIGHT
                    //Go WEST
                    case WEST:
                        return DriveDir.RIGHT;//LEFT);   //Turn LEFT
                    //turn
                    case NORTH:
                        return DriveDir.TURN;

                }

                //From WEST
            case WEST:
                switch(stopDir)
                {
                    //Go NORTH
                    case NORTH:
                        return DriveDir.RIGHT;//LEFT);   //Turn LEFT
                    //Go EAST
                    case WEST://EAST:
                        return DriveDir.FORWARD;   //Go STRAIGHT
                    //Go SOUTH
                    case SOUTH:
                        return DriveDir.LEFT;//RIGHT);   //Turn RIGHT
                    //turn
                    case EAST:
                        return DriveDir.TURN;
                }
        }
        //Invalid direction
        return null;
    }
}

enum Direction{
    NORTH,
    EAST,
    SOUTH,
    WEST
}
