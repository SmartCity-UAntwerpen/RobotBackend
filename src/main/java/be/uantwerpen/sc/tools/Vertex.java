package be.uantwerpen.sc.tools;

import be.uantwerpen.sc.models.map.Node;
import be.uantwerpen.sc.models.map.NodeJson;

import java.util.ArrayList;
import java.util.List;

/**
 * Vertex class
 * Mainly Data class, no function
 * TODO: Usage, comments
 */
public class Vertex
{
    /**
     * Vertex ID
     */
    private Long id;

    /**
     * Adjacent edges of the Vertex
     */
    private List<Edge> adjacencies = new ArrayList<>();

    /**
     * Minimum distance between TODO
     */
    private double minDistance = Double.POSITIVE_INFINITY;

    /**
     * Previous Vertex TODO
     */
    private Vertex previous;

    /**
     * Create Vertex from NodeJson TODO
     * @param nodeJson
     */
    public Vertex(NodeJson nodeJson)
    {
        this.id = nodeJson.getPointEntity().getId();
    }

    /**
     * Create Vertex from Node TODO
     * @param node
     */
    public Vertex(Node node){this.id = node.getNodeId();}

    /**
     * Create Vertex from ID TODO
     * @param id
     */
    public Vertex(Long id){this.id=id;}

    /**
     * Get ID
     * @return Vertex ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Set ID
     * @param id Vertex ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get list of adjacent Edges
     * @return List of Edges
     */
    public List<Edge> getAdjacencies() {
        return adjacencies;
    }

    /**
     * Set adjacent edges
     * @param adjacencies list of all adjacent edges
     */
    public void setAdjacencies(List<Edge> adjacencies) {
        this.adjacencies = adjacencies;
    }

    /**
     * Get min distance between TODO
     * @return Min Distance
     */
    public double getMinDistance() {
        return minDistance;
    }

    /**
     * Set min distance between TODO
     * @param minDistance Min Distance
     */
    public void setMinDistance(double minDistance) {
        this.minDistance = minDistance;
    }

    /**
     * Get previous Vertex TODO
     * @return Previous Vertex
     */
    public Vertex getPrevious() {
        return previous;
    }

    /**
     * Set Previous Vertex TODO
     * @param previous Previous Vertex
     */
    public void setPrevious(Vertex previous) {
        this.previous = previous;
    }

    /**
     * Returns Vertex ID as string
     * @return Vertex ID
     */
    @Override
    public String toString() {
        return "Vertex{" + "id=" + id + '}';
    }
}
