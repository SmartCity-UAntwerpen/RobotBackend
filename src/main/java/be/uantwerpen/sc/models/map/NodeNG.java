package be.uantwerpen.sc.models.map;

import be.uantwerpen.sc.models.OLD.LinkNG;
import be.uantwerpen.sc.models.OLD.Point;

import java.util.List;

/**
 * Node of Map
 * Crossing
 */
public class NodeNG {

    /**
     * Node Id
     */
    private Long nodeId;

    /**
     * Connected Point
     */
    private Point pointEntity;

    /**
     * List of neighbouring links
     */
    private List<LinkNG> neighbours;

    /**
     * Constructor, sets Node ID to Point ID
     * @param pointEntity Point to connect a node to
     */
    public NodeNG(Point pointEntity) {
        this.pointEntity = pointEntity;
        this.nodeId = pointEntity.getId();
    }

    /**
     * Returns Node ID
     * @return Node ID
     */
    public Long getNodeId() {
        return nodeId;
    }

    /**
     * Sets Node ID
     * @param nodeId new ID of node
     */
    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * Returns connected Point
     * @return Point
     */
    public Point getPointEntity() {
        return pointEntity;
    }

    /**
     * Sets connected Point
     * @param pointEntity Point to set
     */
    public void setPointEntity(Point pointEntity) {
        this.pointEntity = pointEntity;
    }

    /**
     * Get neighbouring Links
     * @return List of Links
     */
    public List<LinkNG> getNeighbours() {
        return neighbours;
    }

    /**
     * Set neighbouring Links
     * @param neighbours List of Links
     */
    public void setNeighbours(List<LinkNG> neighbours) {
        this.neighbours = neighbours;
    }

    @Override
    public String toString() {
        return "Node{" +
                "nodeId=" + nodeId +
                ", pointEntity=" + pointEntity +
                ", neighbours=" + neighbours +
                '}';
    }
}
