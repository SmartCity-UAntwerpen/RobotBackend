package be.uantwerpen.sc.services;


import be.uantwerpen.rc.models.map.Link;
import be.uantwerpen.rc.models.map.Map;
import be.uantwerpen.rc.models.map.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Dieter 2018-2019
 * @author Riad 2019-2020
 * <p>
 * Map Control Service
 */
@Service
public class MapControlService {

    @Value("${spring.database.driverClassName}")
    private String driver;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

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

    public boolean loadMap(String mapSQL)
    {
        boolean success;
        try {
            Class.forName(driver);  // checks if a class descriptor can be made of the given driver string.
                                    // If not --> this class doesn't exist, no driver can be used and a exception should be thrown
            Connection connection = DriverManager.getConnection(url, username, password);
            String sqlQueries[] = mapSQL.split(";");
            for (String sqlQuery : sqlQueries) {
                PreparedStatement preparedStatement = connection.prepareStatement((sqlQuery + ";"));
                preparedStatement.execute();
            }
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


    public void resetMap() {
        map = buildMap();
        for (Point v : map.getPointList()) {
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
        for (Point point : pointControlService.getAllPoints()) {
            List<Link> targetLinks = linkEntityList.stream().filter(item -> Objects.equals(item.getStartPoint(), point.getId())).collect(Collectors.toList());
            point.setNeighbours(targetLinks);
            this.map.addPoint(point);
        }
        this.map.setBotEntities(botControlService.getAllBots());
        this.map.setTrafficlightEntity(trafficLightControlService.getAllTrafficLights());
        return this.map;
    }

}