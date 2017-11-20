package be.uantwerpen.sc.services;

import be.uantwerpen.sc.models.*;
import be.uantwerpen.sc.models.map.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Map Control Service
 * TODO
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

    /**
     * BackBone IP (Probably)(TODO)
     */
    @Value("${backbone.ip:default}")
    String coreIp;
    /**
     * BackBone Port (Probably)(TODO)
     */
    @Value("${backbone.port:default}")
    String corePort;

    /**
     * Creates map from DB links
     * @return Created Map
     */
    public Map buildMap()
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

    /**
     * Creates JSON Map from DB links
     * @return Created JSON Map
     */
    public MapJson buildMapJson()
    {
        MapJson mapJson = new MapJson();

        List<Link> linkEntityList = linkControlService.getAllLinks();

        for(Point point : pointControlService.getAllPoints())
        {
            NodeJson nodeJson = new NodeJson(point);

            List<Neighbour> neighbourList = new ArrayList<Neighbour>();

            //TODO: Can be simplified?
            for(Link link: linkEntityList)
            {
                if((link.getStartPoint().getId()) == (nodeJson.getPointEntity().getId()))
                {
                    neighbourList.add(new Neighbour(link));
                }
            }

            nodeJson.setNeighbours(neighbourList);
            mapJson.addNodeJson(nodeJson);
        }

        mapJson.setSize(mapJson.getNodeJsons().size());

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
