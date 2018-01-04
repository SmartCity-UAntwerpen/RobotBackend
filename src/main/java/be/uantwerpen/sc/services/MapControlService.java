package be.uantwerpen.sc.services;

import be.uantwerpen.sc.models.*;
import be.uantwerpen.sc.models.map.*;
import be.uantwerpen.sc.tools.Edge;
import be.uantwerpen.sc.tools.EdgeNG;
import be.uantwerpen.sc.tools.Vertex;
import be.uantwerpen.sc.tools.VertexNG;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Map Control Service
 */
@Service
public class MapControlService
{
    /**
     * Autowired Point Control Service
     */
    @Autowired
    private PointControlService pointControlService;

    /**
     * Autowired Link Control Service
     */
    @Autowired
    private LinkNGControlService linkNGControlService;

    /**
     * Autowired Link Control Service
     */
    @Autowired
    private LinkControlService linkControlService;
    /**
     * Autowired Bot Control Service
     */
    @Autowired
    private BotControlService botControlService;

    /**
     * Autowired TrafficLight Control Service
     */
    @Autowired
    private TrafficLightControlService trafficLightControlService;

    private Map map=null;
    private List<Vertex> vertexMap=null;
    private MapNG mapNG=null;
    private List<VertexNG> vertexMapNG=null;
    public Map getMap() {
        if (map==null)
            map=buildMap();
        return map;
    }

    public MapNG getMapNG() {
        if (mapNG==null)
            mapNG=buildMapNG();
        return mapNG;
    }
    public List<Vertex> getVertexMap(){
        if(vertexMap==null)
            vertexMap=mapToVertexes();
        return vertexMap;
    }
    public List<VertexNG> getVertexMapNG(){
        if(vertexMapNG==null)
            vertexMapNG=mapToVertexesNG();
        return vertexMapNG;
    }

    /**
     * What the fuck, optimise this shit.
     * Quadruple nested for?
     * Really?
     * @return
     */
    private List<Vertex> mapToVertexes(){
        List<Vertex> vertexes = new ArrayList<>();
        for (Node n : getMap().getNodeList())
            vertexes.add(new Vertex(n));

        ArrayList<Edge> edges;
        List<ArrayList<Edge>> edgeslistinlist = new ArrayList<>();
        int i = 0;
        for (Node node : getMap().getNodeList()){
            edges = new ArrayList<>();
            for (Link neighbour : node.getNeighbours()){
                for (Vertex v : vertexes){
                    if(Objects.equals(v.getId(), neighbour.getStopPoint().getId())){
                        edges.add(new Edge(v.getId(),neighbour.getWeight(),neighbour));
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
    public void resetVertex(){
        for(Vertex v: getVertexMap()){
            v.setMinDistance(Double.POSITIVE_INFINITY);
            v.setPrevious(null);
        }
    }
    public void resetVertexNG(){
        for(VertexNG v: getVertexMapNG()){
            v.setMinDistance(Double.POSITIVE_INFINITY);
            v.setPrevious(null);
        }
    }
    /**
     * Gets all links and points from the database
     * Connects all links to their specific nodes and sets them as neighbors
     * Adds all these nodes to the map
     * Map creates both real (Correct RFID) as simulated (letter of the alphabet) maps
     * @return Created Map
     */
    private Map buildMap()
    {
        Map map = new Map();

        List<Link> linkEntityList = linkControlService.getAllLinks();

        for(Point point : pointControlService.getAllPoints())
        {
            Node node = new Node(point);
            List<Link> targetLinks = linkEntityList.stream().filter(item -> Objects.equals(item.getStartPoint().getId(), node.getNodeId())).collect(Collectors.toList());

            node.setNeighbours(targetLinks);
            map.addNode(node);
        }

        map.setBotEntities(botControlService.getAllBots());
        map.setTrafficlightEntity(trafficLightControlService.getAllTrafficLights());

        return map;
    }

    public MapNG buildMapNG()
    {
        MapNG map = new MapNG();
        List<LinkNG> linkEntityList = linkNGControlService.getAllLinks();

        for(Point point : pointControlService.getAllPoints())
        {
            NodeNG node = new NodeNG(point);
            List<LinkNG> targetLinks = linkEntityList.stream().filter(item -> Objects.equals(item.getStartPoint().getId(), node.getNodeId())).collect(Collectors.toList());

            node.setNeighbours(targetLinks);
            map.addNode(node);
        }

        map.setBotEntities(botControlService.getAllBots());
        map.setTrafficlightEntity(trafficLightControlService.getAllTrafficLights());

        return map;
    }
    private List<VertexNG> mapToVertexesNG(){
        List<VertexNG> vertexes = new ArrayList<>();
        for (NodeNG n : getMapNG().getNodeList())
            vertexes.add(new VertexNG(n));

        ArrayList<EdgeNG> edges;
        List<ArrayList<EdgeNG>> edgeslistinlist = new ArrayList<>();
        int i = 0;
        for (NodeNG node : getMapNG().getNodeList()){
            edges = new ArrayList<>();
            for (LinkNG neighbour : node.getNeighbours()){
                for (VertexNG v : vertexes){
                    if(Objects.equals(v.getId(), neighbour.getStopPoint().getId())){
                        edges.add(new EdgeNG(v.getId(),neighbour.getWeight(),neighbour));
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
    /**
     * Gets all links and points from the database
     * Connects all links to their specific nodes and sets them as neighbors
     * Adds all these nodes to the map
     * Map creates both real (Correct RFID) as simulated (letter of the alphabet) maps
     * @return Created JSON Map
     */
    public MapJson buildMapJson()
    {
        MapJson mapJson = new MapJson();
        List<Link> linkEntityList = linkControlService.getAllLinks();
        List<Point> points=pointControlService.getAllPoints();
        for(Point point : points)
        {
            NodeJson nodeJson = new NodeJson(point);
            List<Neighbour> neighbourList = new ArrayList<>();

            for(Link link: linkEntityList)
                if(Objects.equals(link.getStartPoint().getId(), nodeJson.getPointEntity().getId()))
                    neighbourList.add(new Neighbour(link));

            nodeJson.setNeighbours(neighbourList);
            mapJson.addNodeJson(nodeJson);
        }

        return mapJson;
    }

    /**
     * Creates MapNew based on DB Links and points
     * TODO: MapNew renaming
     * @return MapNew
     */
    public MapNew buildNewMap(){
        MapNew mapNew = new MapNew();
        mapNew.setLinkList(linkControlService.getAllLinks());
        mapNew.setPointList(pointControlService.getAllPoints());

        return mapNew;
    }
}
