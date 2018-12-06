package be.uantwerpen.sc.models.map;

import be.uantwerpen.sc.tools.VertexNG;

import java.util.ArrayList;
import java.util.List;

/**
 * Path Data Class
 * The path the robot will follow
 */
public class PathNG {

    private List<VertexNG> path;

    public PathNG(List<VertexNG> path) {
        this.path = path;
    }

    public PathNG() {
        this.path =new ArrayList<>();
    }

    public double getWeight(){
       return path.get(path.size()-1).getMinDistance();
    }
    public void addVertex(VertexNG vertex){this.path.add(vertex);}
    public List<VertexNG> getPath() {
        return path;
    }
}
