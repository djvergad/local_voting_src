/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import application.Statistics;
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

    public ArrayList<Node> nodes;
    ArrayList<Slot> slots;
    public Statistics stats;

    boolean findSlot(Node node) {
        for (Slot slot : slots) {
            if (slot.addNode(node)) {
                return true;
            }
        }
        return false;
    }

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
        
        source.distance = distance;
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

    public void dumpSlots() {
        System.out.println("Dumping network");
        for (Slot slot : slots) {
            System.out.println(this.slots.indexOf(slot) + ": " + slot);
        }
        System.out.println(this.slots.size());
    }

    public void dumpQueues() {
        for (Node node : nodes) {
            if (!node.backlog.isEmpty()) {
                System.out.print(node + ":" + node.backlog.size() + " ");
            }
        }
        System.out.println();
    }

    boolean isConnected() {
        for (Node node : nodes) {
            if (node.routingtable.size() < nodes.size() - 1) {
                return false;
            }
        }
        return true;
    }
}
