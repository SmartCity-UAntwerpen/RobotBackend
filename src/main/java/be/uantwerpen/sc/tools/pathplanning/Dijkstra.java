package be.uantwerpen.sc.tools.pathplanning;

import be.uantwerpen.sc.tools.Edge;
import be.uantwerpen.sc.tools.Vertex;

import java.util.*;

/**
 * Calculates Dijkstra Path for Robot
 */
public class Dijkstra
{
    /**
     * Computes Dijkstra path based on Source Vertex and list of possible Vertices to visit
     * @param sourceID Source ID of Vertex
     * @param vertexes Map Vertices
     */
    public void computePaths(int sourceId, List<Vertex> vertexes)
    {
        Vertex source=getVertexByID(vertexes, sourceId);
        source.setMinDistance(0);
        Queue<Vertex> vertexQueue = new LinkedList<>();
        vertexQueue.add(source);
        List<Vertex> vertexList = new ArrayList<>();
        while (!vertexQueue.isEmpty()) {
            Vertex u = vertexQueue.poll();
            Vertex v = new Vertex(1L);
            // Visit each edge exiting u
            for (Edge e : u.getAdjacencies())
            {
                for (Vertex w : vertexes){
                    if(w.getId() == e.getTarget()){
                        v =w;
                        vertexList.add(v);
                    }
                }

                double weight = e.getWeight();
                double distanceThroughU = u.getMinDistance() + weight;
                if (distanceThroughU < v.getMinDistance()) {
                    vertexQueue.remove(v);

                    v.setMinDistance(distanceThroughU) ;
                    v.setPrevious(u);
                    vertexQueue.add(v);
                }
            }
        }
    }

    /**
     * Calculates shortest path between given Vertex and list of available Vertices
     * Better alternative to arraylist possible?
     * @param targetId End ID of Vertex
     * @param vertexes List of available Vertices
     * @return
     */
    public List<Vertex> getShortestPathTo(int targetId, List<Vertex> vertexes)
    {
        Vertex target=getVertexByID(vertexes, targetId);
        List<Vertex> path = new ArrayList<Vertex>();
        for (Vertex vertex = target; vertex != null;  vertex = vertex.getPrevious())
            path.add(vertex);

        /*
        Vertex vertex = target;
        path.add(vertex);
        i = vertex.getPrevious();
        vertex= vertexes.get(i);


        int i = (int)(target.getId() % Integer.MAX_VALUE);
        do {
            for (Vertex v : vertexes){
                if(v.getId() == i) {
                    path.add(getVertexByID(vertexes,i - 1));
                    break;
                }
            }
            try {
                i = (int)(getVertexByID(vertexes,i-1).getPrevious().getId() % Integer.MAX_VALUE);
            }catch (Exception e){
               i = 0;
            }
        } while (i != 0);

       /* for (int i = target.getId(); vertexes.get(i).getPrevious() != null; i = vertexes.get(i).getPrevious()){
            for (Vertex v : vertexes){
                if(v.getId() == i)
                    path.add(vertexes.get(i-1));
            }
        }*/


        Collections.reverse(path);
        return path;
    }

    private Vertex getVertexByID(List<Vertex> list, int target){
        for(Vertex v : list){
            if(v.getId()==target)
                return v;
        }
        return null;
    }
}
