package be.uantwerpen.sc.tools;

import be.uantwerpen.sc.models.map.newMap.Link;


/**
 * Edge Class
 * Mainly data, no function
 * Edge Links of a vertex
 */
public class Edge
{
    /**
     * Edge Target
     */
    private Long target;

    /**
     * Weight of Edge
     */
    private int weight;

    /**
     * Link corresponding to this edge
     */
    private Link linkEntity;

    /**
     * Create Edge using given Target, Weight and Link
     * @param argTarget
     * @param argWeight
     * @param linkEntity
     */
    public Edge(Long argTarget, int argWeight, Link linkEntity)
    {
        target = argTarget;
        weight = argWeight;
        this.linkEntity = linkEntity;
    }

    /**
     * Return Target
     * @return Edge Target
     */
    public Long getTarget()
    {
        return target;
    }

    /**
     * Return Weight
     * @return Edge Weight
     */
    public int getWeight()
    {
        return weight;
    }


    /**
     * Return Edge Link
     * @return Edge Link
     */
    public Link getLinkEntity()
    {
        return linkEntity;
    }
}
