package be.uantwerpen.sc.models.map;

import be.uantwerpen.sc.models.Link;
import be.uantwerpen.sc.tools.pathplanning.AbstractMap;
import be.uantwerpen.sc.models.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dries on 8-6-2017.
 */
public class MapNew implements AbstractMap
{
    private List<Point> pointList;
    private List<Link> linkList;

    public MapNew(){
        pointList = new ArrayList<>();
        linkList = new ArrayList<>();
    }

    public void addPoint(Point point){
        pointList.add(point);
    }

    public void addLink(Link link){
        linkList.add(link);
    }

    public void setLinkList(List<Link> linkList) {
        this.linkList = linkList;
    }

    public List<Link> getLinkList()
    {
        return linkList;
    }

    public void setPointList(List<Point> pointList) {
        this.pointList = pointList;
    }

    public List<Point> getPointList()
    {
        return pointList;
    }

    @Override
    public String toString() {
        return "AbstractMap{" +
                "pointList=" + pointList +
                ", linkList=" + linkList +
                '}';
    }
}
