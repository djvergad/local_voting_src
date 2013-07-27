/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stability.network;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import stability.Packet;

/**
 *
 * @author bruker
 */
public class Node {

    Queue<Packet> backlog = new PriorityQueue<>();
    Map<Node, Node> routingtable;
    Set<Node> neighbors = new HashSet<>();
    double x;
    double y;
    int id;
    static int id_helper;
    double weight = 1;

    public Node() {
        id = id_helper++;
    }

    public void dump() {
        System.out.println("Node: " + id
                + " X= " + x
                + " Y= " + y
                + " Neighbors: " + Arrays.toString(neighbors.toArray()));
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
