package be.uantwerpen.sc.services;


import be.uantwerpen.rc.models.map.Link;
import be.uantwerpen.rc.models.map.Map;
import be.uantwerpen.rc.models.map.Point;
import be.uantwerpen.sc.controllers.MapController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private Map map = null;

    private Logger logger = LoggerFactory.getLogger(MapControlService.class);

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





    public Map getMap()
    {
        return buildMap();
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
        String driver = "${spring.database.driverClassName:default}";
        String url = "${spring.datasource.url:default}";
        try {
            Class.forName(driver);  // checks if a class descriptor can be made of the given driver string.
                                    // If not --> this class doesn't exist, no driver can be used and a exception should be thrown
            Connection connection = DriverManager.getConnection(url, "${spring.datasource.username:default}", "${spring.datasource.password:default}");
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

    public List<Point> getVertexMap() {
        //Always update the map
        this.map = this.buildMap();
        return mapToVertexes();
    }

    /**
     * What the fuck, optimise this shit.
     * Quadruple nested for?
     * Really?
     *
     * @return List of vertexes
     */
    private List<Point> mapToVertexes() {
        List<Point> vertexes = new ArrayList<>();
        vertexes.addAll(getMap().getPointList());

        ArrayList<Link> edges;
        List<ArrayList<Link>> edgeslistinlist = new ArrayList<>();
        int i = 0;
        for (Point node : getMap().getPointList()) {
            edges = new ArrayList<>();
            for (Link neighbour : node.getNeighbours()) {
                for (Point v : vertexes) {
                    if (Objects.equals(v.getId(), neighbour.getEndPoint())) {
                        edges.add(new Link(v.getId(), neighbour.getCost().getWeight()));
                    }
                }
            }
            edgeslistinlist.add(i, (edges));
            i++;
        }
        for (int j = 0; j < vertexes.size(); j++) {//Todo: zet dit in de quatro 4 loop
            vertexes.get(j).setNeighbours(edgeslistinlist.get(j));
        }
        return vertexes;
    }


    public void resetVertex() {
        for (Point v : getVertexMap()) {
            v.setMinDistance(Double.POSITIVE_INFINITY);
            v.setPrevious(null);
        }
    }

    /**
     * Gets all links and points from the database
     * Connects all links to their specific nodes and sets them as neighbours
     * Adds all these nodes to the map
     * Map creates both real (Correct RFID) as simulated (letter of the alphabet) maps
     *
     * @return Created Map
     */
    private Map buildMap() {
        this.map = new Map();
        List<Link> linkEntityList = linkControlService.getAllLinks();
        //logger.info("Link Entity List: " + linkEntityList.toString());
        for (Point point : pointControlService.getAllPoints()) {
            //logger.info("Point: " + point.toString());
            List<Link> targetLinks = linkEntityList.stream().filter(item -> Objects.equals(item.getStartPoint(), point.getId())).collect(Collectors.toList());
            //logger.info("Target Links: " + targetLinks.toString());


            point.setNeighbours(targetLinks);
            this.map.addPoint(point);
        }

        //logger.info("Map: " + map.toString());

        this.map.setBotEntities(botControlService.getAllBots());
        this.map.setTrafficlightEntity(trafficLightControlService.getAllTrafficLights());

        return this.map;
    }

}