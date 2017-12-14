package be.uantwerpen.sc.models.map;

import be.uantwerpen.sc.tools.pathplanning.AbstractMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Niels on 14/04/2016.
 */
public class MapJson implements AbstractMap
{
    private List<NodeJson> nodeJsons;

    public MapJson() {
        this.nodeJsons = new ArrayList<>();
    }

    public void addNodeJson(NodeJson nodeJson){
        nodeJsons.add(nodeJson);
    }

    public List<NodeJson> getNodeJsons() {
        return nodeJsons;
    }

    public void setNodeJsons(List<NodeJson> nodeJsons) {
        this.nodeJsons = nodeJsons;
    }



    @Override
    public String toString() {
        return "MapJson{" +
                "nodeJsons=" + nodeJsons +
                '}';
    }
}
