package be.uantwerpen.sc.services;


import be.uantwerpen.rc.models.map.Link;
import be.uantwerpen.rc.models.map.Map;
import be.uantwerpen.rc.models.map.Node;
import be.uantwerpen.rc.models.map.Point;
import be.uantwerpen.rc.tools.Edge;
import be.uantwerpen.rc.tools.Vertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Dieter 2018-2019
 * <p>
 * Map Control Service
 */
@Service
public class MapControlService {
    /**
     * Autowired Point Control Service
     */
    @Autowired
    private PointControlService pointControlService;

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

    private Map map = null;
    private List<Vertex> vertexMap = null;

    public Map getMap() {
        //if (map==null)
        map = buildMap();
        return map;
    }

    /**
     * Update map
     * Update the map
     */
    public void updateMap() {
        map = buildMap();
    }

    public boolean loadMap(String mapSQL)
    {
        boolean success;
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://smartcity.ddns.net:3306";
        try {
            Class.forName(driver);  // checks if a class descriptor can be made of the given driver string.
                                    // If not --> this class doesn't exist and a exception should be thrown
            Connection connection = DriverManager.getConnection(url, "smartcity", "smartcity");
            PreparedStatement preparedStatement = connection.prepareStatement(mapSQL);
            preparedStatement.execute();
            connection.close();
            success = true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("ClassNotFoundException: class of driver + " + driver + " is not found!");
            success = false;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQLException: error executing mapSQL-script");
            success = false;
        }
        return success;
    }

    public List<Vertex> getVertexMap() {
        //Always update the map
        this.buildMap();
        //if(vertexMap==null)
        vertexMap = mapToVertexes();
        return vertexMap;
    }

    /**
     * What the fuck, optimise this shit.
     * Quadruple nested for?
     * Really?
     *
     * @return List of vertexes
     */
    private List<Vertex> mapToVertexes() {
        List<Vertex> vertexes = new ArrayList<>();
        for (Node n : getMap().getNodeList())
            vertexes.add(new Vertex(n));

        ArrayList<Edge> edges;
        List<ArrayList<Edge>> edgeslistinlist = new ArrayList<>();
        int i = 0;
        for (Node node : getMap().getNodeList()) {
            edges = new ArrayList<>();
            for (Link neighbour : node.getNeighbours()) {
                for (Vertex v : vertexes) {
                    if (Objects.equals(v.getId(), neighbour.getEndPoint().getId())) {
                        edges.add(new Edge(v.getId(), neighbour.getWeight(), neighbour));
                    }
                }
            }
            edgeslistinlist.add(i, (edges));
            i++;
        }
        for (int j = 0; j < vertexes.size(); j++) {//Todo: zet dit in de quatro 4 loop
            vertexes.get(j).setAdjacencies(edgeslistinlist.get(j));
        }
        return vertexes;
    }


    public void resetVertex() {
        for (Vertex v : getVertexMap()) {
            v.setMinDistance(Double.POSITIVE_INFINITY);
            v.setPrevious(null);
        }
    }

    /**
     * Gets all links and points from the database
     * Connects all links to their specific nodes and sets them as neighbors
     * Adds all these nodes to the map
     * Map creates both real (Correct RFID) as simulated (letter of the alphabet) maps
     *
     * @return Created Map
     */
    private Map buildMap() {
        Map map = new Map();

        List<Link> linkEntityList = linkControlService.getAllLinks();

        for (Point point : pointControlService.getAllPoints()) {
            Node node = new Node(point);
            List<Link> targetLinks = linkEntityList.stream().filter(item -> Objects.equals(item.getStartPoint().getId(), node.getNodeId())).collect(Collectors.toList());

            node.setNeighbours(targetLinks);
            map.addNode(node);
        }

        map.setBotEntities(botControlService.getAllBots());
        map.setTrafficlightEntity(trafficLightControlService.getAllTrafficLights());

        return map;
    }

}