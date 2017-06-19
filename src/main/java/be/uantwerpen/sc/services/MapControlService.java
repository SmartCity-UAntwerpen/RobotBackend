package be.uantwerpen.sc.services;

import be.uantwerpen.sc.models.*;
import be.uantwerpen.sc.models.map.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Created by Niels on 14/04/2016.
 */
@Service
public class MapControlService
{
    @Autowired
    private PointControlService pointControlService;

    @Autowired
    private LinkControlService linkControlService;

    @Autowired
    private BotControlService botControlService;

    @Autowired
    private TrafficLightControlService trafficLightControlService;

    String coreIp = "143.129.39.151";
    String corePort = "10000";

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

    public MapJson buildMapJson()
    {
        MapJson mapJson = new MapJson();

        List<Link> linkEntityList = linkControlService.getAllLinks();

        for(Point point : pointControlService.getAllPoints())
        {
            NodeJson nodeJson = new NodeJson(point);

            List<Neighbour> neighbourList = new ArrayList<Neighbour>();

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

    public MapNew buildNewMap(){
        MapNew mapNew = new MapNew();
        mapNew.setLinkList(linkControlService.getAllLinks());
        mapNew.setPointList(pointControlService.getAllPoints());

        return mapNew;
    }
}
