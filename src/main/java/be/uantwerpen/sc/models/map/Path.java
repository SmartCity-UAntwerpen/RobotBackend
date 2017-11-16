package be.uantwerpen.sc.models.map;

import be.uantwerpen.sc.tools.Vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * Path Data Class
 * TODO WHAT DIS
 */
public class Path {

    private List<Vertex> path;

    public Path(List<Vertex> path) {
        this.path = path;
    }

    public Path() {
        path = new ArrayList<>();
    }

    public void addVertex(Vertex vertex){this.path.add(vertex);}
    public List<Vertex> getPath() {
        return path;
    }
}
