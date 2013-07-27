/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stability.network;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 *
 * @author bruker
 */
public class Network {

    ArrayList<Node> nodes = new ArrayList<>();
    int frameLength;
    ArrayList<Slot> slots;
    
    
       
    void updateRoutes() {
        // Reset the routing tables
        for (Node node : nodes) {
            node.routingtable = new HashMap<>();
            dijkstra(node);
        }
    }

    void dijkstra(Node source) {
        final HashMap<Node, Double> distance = new HashMap<>();
        HashMap<Node, Node> previous = new HashMap<>();
        for (Node node : nodes) {
            distance.put(node, Double.MAX_VALUE);
        }
        PriorityQueue<Node> q = new PriorityQueue<>(nodes.size(), new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return distance.get(o1).compareTo(distance.get(o2));
            }
        });
        distance.put(source, 0D);
        for (Node node : nodes) {
            q.offer(node);
        }

        while (q.size() > 0) {
            Node u = q.poll();
            if (distance.get(u) == Double.MAX_VALUE) {
                break;
            }
            for (Node v : u.neighbors) {
                double alt = distance.get(u) + v.weight;
                if (alt < distance.get(v)) {
                    q.remove(v);
                    distance.put(v, alt);
                    previous.put(v, u);
                    q.offer(v);
                }
            }
        }
//        for (Map.Entry<Node, Double> entry : distance.entrySet()) {
//            System.out.println(" " + entry.getKey() + ":" + entry.getValue());
//        }

//        System.out.println("Previous table of source");
        for (Map.Entry<Node, Node> entry : previous.entrySet()) {
          //  System.out.println(" " + entry.getKey() + ":" + entry.getValue());
            Node dst = entry.getKey();
            Node src = entry.getValue();

            if (src == source) {
                source.routingtable.put(dst, dst);
            } else {
                while ((previous.get(src) != source)) {
                    src = previous.get(src);
                }
                source.routingtable.put(dst, src);
            }
        }
        //System.out.println("Routing table of source");

//        System.out.print("s"+source);
//        for (Map.Entry<Node, Node> entry : source.routingtable.entrySet()) {
//            System.out.print(" " + entry.getKey() + ":" + entry.getValue());
//        }
//        System.out.println();
    }

    public void dump() {
        System.out.println("Network size: " + nodes.size());
        for (Node node : nodes) {
            node.dump();
        }
    }
}
