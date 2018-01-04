package be.uantwerpen.sc.tools;

import be.uantwerpen.sc.models.map.Node;
import be.uantwerpen.sc.models.map.NodeNG;

import java.util.ArrayList;
import java.util.List;

/**
 * Vertex class
 * Mainly Data class, no function
 * Path between 2 Nodes
 */
public class VertexNG
{
    /**
     * Vertex ID
     */
    private Long id;

    /**
     * Adjacent edges of the Vertex
     */
    private List<EdgeNG> adjacencies = new ArrayList<>();

    /**
     * Minimum distance between
     */
    private double minDistance = Double.POSITIVE_INFINITY;

    /**
     * Previous (visited) Vertex
     */
    private VertexNG previous;

    /**
     * Create Vertex linking to node
     * @param node
     */
    public VertexNG(NodeNG node){this.id = node.getNodeId();}

    /**
     * Create Vertex from ID
     * @param id
     */
    public VertexNG(Long id){this.id=id;}

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
    public List<EdgeNG> getAdjacencies() {
        return adjacencies;
    }

    /**
     * Set adjacent edges
     * @param adjacencies list of all adjacent edges
     */
    public void setAdjacencies(List<EdgeNG> adjacencies) {
        this.adjacencies = adjacencies;
    }

    /**
     * Get min distance between
     * @return Min Distance
     */
    public double getMinDistance() {
        return minDistance;
    }

    /**
     * Set min distance between start point
     * @param minDistance Min Distance
     */
    public void setMinDistance(double minDistance) {
        this.minDistance = minDistance;
    }

    /**
     * Get previous Vertex
     * @return Previous Vertex
     */
    public VertexNG getPrevious() {
        return previous;
    }

    /**
     * Set Previous Vertex
     * @param previous Previous Vertex
     */
    public void setPrevious(VertexNG previous) {
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
