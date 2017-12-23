package be.uantwerpen.sc.models.map;

import be.uantwerpen.sc.tools.Vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * Path Data Class
 * The path the robot will follow
 */
public class Path {

    private List<Vertex> path;

    public Path(List<Vertex> path) {
        this.path = path;
    }

    public Path() {
        this.path =new ArrayList<>();
    }

    public double getWeight(){
       return path.get(path.size()-1).getMinDistance();
    }
    public void addVertex(Vertex vertex){this.path.add(vertex);}
    public List<Vertex> getPath() {
        return path;
    }
}
