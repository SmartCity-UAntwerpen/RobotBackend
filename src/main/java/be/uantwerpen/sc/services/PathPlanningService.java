package be.uantwerpen.sc.services;

import be.uantwerpen.sc.models.Link;
import be.uantwerpen.sc.models.map.*;
import be.uantwerpen.sc.tools.pathplanning.Dijkstra;
import be.uantwerpen.sc.tools.Edge;
import be.uantwerpen.sc.tools.Vertex;
import be.uantwerpen.sc.tools.pathplanning.IPathplanning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Service for path planning
 * TODO: Shitty class, remake
 */
@Service
public class PathPlanningService implements IPathplanning
{
    /**
     * Autowired link control service
     */
    @Autowired
    private LinkControlService linkControlService;

    /**
     * Autowired map control service
     */
    @Autowired
    private MapControlService mapControlService;

    /**
     * List of Link entities
     */
    private List<Link> linkEntityList;

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
    public List<Vertex> CalculatePathNonInterface(int start,int stop){
        Map map = mapControlService.buildMap();
        List<Link> linkEntityList = new ArrayList<>();
        List<Vertex> vertexes = new ArrayList<>();
        for (Node nj : map.getNodeList()){
            vertexes.add(new Vertex(nj));
            for(Link linkEntity : nj.getNeighbours()){
                linkEntityList.add(linkEntity);
            }
        }

        ArrayList<Edge> edges;
        List<ArrayList<Edge>> edgeslistinlist = new ArrayList<>();
        Link realLink = new Link();
        int i = 0;
        for (Node nj : map.getNodeList()){
            edges = new ArrayList<>();
            for (Link neighbour : nj.getNeighbours()){
                for (Vertex v : vertexes){
                    if(v.getId() == neighbour.getStopPoint().getId()){
                        for(Link linkEntity: linkEntityList){
                            if(linkEntity.getStopPoint().getId() == v.getId() && linkEntity.getStartPoint().getId() == nj.getPointEntity().getId()){
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
        //System.out.println("Distance to " + vertexes.get(stop-1) + ": " + vertexes.get(stop-1).getMinDistance());
        List<Vertex> path = dijkstra.getShortestPathTo(stop,vertexes);
        //System.out.println("Path: " + path);
        //return ("Distance to " + vertexes.get(stop-1) + ": " + vertexes.get(stop-1).minDistance) + ( "Path: " + path);
        return path;
    }

    /**
     * Calculates path following Dijkstra
     * TODO Hardcoded Dijkstra
     * @param start start int (?)
     * @param stop stop int (?)
     * @param links List of Links
     * @return List of Vertexes following the shortest path
     */
    @Override
    public List<Vertex> Calculatepath(int start, int stop, List<Link> links) {
        List<Vertex> vertexes = mapToVertexes(links);

        dijkstra.computePaths(start,vertexes);
        List<Vertex> path = dijkstra.getShortestPathTo(stop,vertexes);
        return path;
    }

    /**
     *
     * @param map
     * @param start
     * @param links
     * @return
     */
    @Override
    public List<Vertex> nextRandomPath(Map map, int start, List<Link> links) {
        List<Vertex> vertexes = mapToVertexes(links);

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
        return vertexList;
    }

    /**
     * What the fuck, optimise this shit.
     * Quadruple nested for?
     * Really?
     * @param links
     * @return
     */
    private List<Vertex> mapToVertexes(List<Link> links){
        MapJson mapJsonServer = mapControlService.buildMapJson();
        List<Vertex> vertexes = new ArrayList<>();

        for (NodeJson nj : mapJsonServer.getNodeJsons())
            vertexes.add(new Vertex(nj));

        ArrayList<Edge> edges;
        List<ArrayList<Edge>> edgeslistinlist = new ArrayList<>();
        Link realLink = new Link();
        int i = 0;
        for (NodeJson node : mapJsonServer.getNodeJsons()){//TODO: double getNodeJsons, single request
            edges = new ArrayList<>();
            for (Neighbour neighbour : node.getNeighbours()){
                for (Vertex v : vertexes){
                    if(v.getId() == neighbour.getPointEntity().getId()){

                        //Check of 2 edges een van de links bevat
                        for(Link linkEntity: links){
                            if(linkEntity.getStopPoint().getId() == v.getId() && linkEntity.getStartPoint().getId() == node.getPointEntity().getId()){
                                //System.out.println(linkEntity.toString() +" " + linkEntity);
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

        for (int j = 0; j < vertexes.size();j++){//Todo: zet dit in de quatro 4 loop
            vertexes.get(j).setAdjacencies(edgeslistinlist.get(j));
        }

        return vertexes;
    }
}
