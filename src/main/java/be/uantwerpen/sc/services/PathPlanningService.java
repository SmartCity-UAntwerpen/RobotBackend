package be.uantwerpen.sc.services;

import be.uantwerpen.sc.models.Link;
import be.uantwerpen.sc.models.LinkNG;
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

    public PathNG CalculatePathNG(int start, int stop) {
        List<VertexNG> vertexes = mapControlService.getVertexMapNG();
        mapControlService.resetVertexNG();
        dijkstra.computePathsNG(start,vertexes);
        return dijkstra.getShortestPathToNG(stop,vertexes);
    }
    @Override
    public double CalculatePathWeight(int start, int stop){
        Path p=CalculatePath(start,stop);
        return p.getWeight();
    }
    public double CalculatePathLength(int start, int stop){
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

    public List<DriveDirEnum> createBotDriveDirs(Path path){
        List<DriveDirEnum> commands=new ArrayList<>();
        List<Vertex> vertices=path.getPath();
        Collections.reverse(vertices);
        List<Link> links=new LinkedList<>();
        for (Vertex v: vertices) {
            if(v.getPrevious()==null) {
                commands.add(DriveDirEnum.FOLLOW);
                commands.add(DriveDirEnum.FORWARD);
                break;
            }
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
            DriveDirEnum relDir=getNextRelDir(start,stop);
            commands.add(relDir);
            commands.add(DriveDirEnum.FOLLOW);
            previous=l;
        }
        return commands;
    }
    public DriveDirEncapsulator createBotDriveDirsNG(PathNG path){
        DriveDirEncapsulator commands=new DriveDirEncapsulator();
        List<VertexNG> vertices=path.getPath();
        Collections.reverse(vertices);
        List<LinkNG> links=new LinkedList<>();
        for (VertexNG v: vertices) {
            if(v.getPrevious()==null) {
                commands.addDriveDir(new DriveDir(DriveDirEnum.FORWARD));
                commands.addDriveDir(new DriveDir(DriveDirEnum.FOLLOW));
                break;
            }
            for(EdgeNG l:v.getPrevious().getAdjacencies()) {
                if(Objects.equals(l.getTarget(), v.getId()))
                    links.add(l.getLinkEntity());
            }
        }
        Collections.reverse(links);
        LinkNG previous=null;
        for(LinkNG l: links) {
            if(previous==null) {
                previous=l;
                continue;
            }
            if(l.getAngle()==null){
                commands.addDriveDir(new DriveDir(DriveDirEnum.FOLLOW));
            }
            else if(l.getAngle()==0){
                commands.addDriveDir(new DriveDir(DriveDirEnum.FORWARD));
            }
            else if(l.getAngle()<0) {
                DriveDir d=new DriveDir(DriveDirEnum.LEFT);
                d.setAngle(-l.getAngle());
               commands.addDriveDir(d);
            }
            else if(l.getAngle()==180){
                commands.addDriveDir(new DriveDir(DriveDirEnum.TURN));
            }
            else{
                DriveDir d=new DriveDir(DriveDirEnum.RIGHT);
                d.setAngle(l.getAngle());
                commands.addDriveDir(d);
            }

            //commands.addDriveDir(new DriveDir(DriveDirEnum.FOLLOW));
            previous=l;
        }
        return commands;
    }
    private Direction getDirection(String s){
        switch (s){
            case "Z":
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
    private DriveDirEnum getNextRelDir(Direction startDir, Direction stopDir){
        //Calculate relative direction
        switch(startDir)
        {
            //From NORTH
            case NORTH:
                switch(stopDir)
                {
                    //Go EAST
                    case EAST:
                        return DriveDirEnum.RIGHT;//LEFT);   //Turn LEFT
                    //Go SOUTH
                    case NORTH://SOUTH:
                        return DriveDirEnum.FORWARD;   //Go STRAIGHT
                    //Go WEST
                    case WEST:
                        return DriveDirEnum.LEFT;//RIGHT);   //Turn RIGHT
                    //turn
                    case SOUTH:
                        return DriveDirEnum.TURN;

                }

                //From EAST
            case EAST:
                switch(stopDir)
                {
                    //Go NORTH
                    case NORTH:
                        return DriveDirEnum.LEFT;//RIGHT);   //Turn RIGHT
                    //Go SOUTH
                    case SOUTH:
                        return DriveDirEnum.RIGHT;//LEFT);   //Turn LEFT
                    //Go WEST
                    case EAST://WEST:
                        return DriveDirEnum.FORWARD;   //Go STRAIGHT
                    //turn
                    case WEST:
                        return DriveDirEnum.TURN;
                }

                //From SOUTH
            case SOUTH:
                switch(stopDir)
                {
                    //Go NORTH
                    case SOUTH://NORTH:
                        return DriveDirEnum.FORWARD;   //Go STRAIGHT
                    //Go EAST
                    case EAST:
                        return DriveDirEnum.LEFT;//RIGHT);   //Turn RIGHT
                    //Go WEST
                    case WEST:
                        return DriveDirEnum.RIGHT;//LEFT);   //Turn LEFT
                    //turn
                    case NORTH:
                        return DriveDirEnum.TURN;

                }

                //From WEST
            case WEST:
                switch(stopDir)
                {
                    //Go NORTH
                    case NORTH:
                        return DriveDirEnum.RIGHT;//LEFT);   //Turn LEFT
                    //Go EAST
                    case WEST://EAST:
                        return DriveDirEnum.FORWARD;   //Go STRAIGHT
                    //Go SOUTH
                    case SOUTH:
                        return DriveDirEnum.LEFT;//RIGHT);   //Turn RIGHT
                    //turn
                    case EAST:
                        return DriveDirEnum.TURN;
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
