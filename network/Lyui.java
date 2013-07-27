/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import simulator.Simulator;

/**
 *
 * @author dimitriv
 */
public class Lyui extends DRAND {
    
    public Lyui(Network network, Simulator simulator, double slotTime) {
        super(network, simulator, slotTime);
        
        execLyui();
//        network.dumpSlots();
    }
    final Map<Node, Integer> defaultColor = new HashMap<>();
    
    final void execLyui() {

        // Create defaultColor table and remove slot assignments (from DRAND)

        for (Node node : network.nodes) {
            defaultColor.put(node, network.slots.indexOf(node.reservations.peek().slot));
        }
        for (Node node : network.nodes) {
            node.reservations.remove();
        }
        
        network.slots.clear();

        // Calculate p_k for each node
        Map<Node, Integer> p_ck = new HashMap<>();
        int max_p = 0;
        for (Entry<Node, Integer> entry : defaultColor.entrySet()) {
            int p = calcP_k(entry.getValue());
            max_p = p > max_p ? p : max_p;
            p_ck.put(entry.getKey(), p);
        }
        
        // Set 2*map_p for te Lobats extension... Should not affect the Lyui one
        for (int t = 0; t < 2 * max_p; t++) {
            // List of candidate nodes for slot t
            PriorityQueue<Node> queue = new PriorityQueue<>(network.nodes.size(), new Comparator<Node>() {
                @Override
                public int compare(Node o1, Node o2) {
                    return -defaultColor.get(o1).compareTo(defaultColor.get(o2));
                }
            });
            // Add the node to list according to Lyui's criterion
            for (Node node : network.nodes) {
                if (t % p_ck.get(node) == (defaultColor.get(node) % p_ck.get(node))) {
                    queue.offer(node);
                }
            }

            // Create a slot and add it to the network
            Slot slot = new Slot();
            network.slots.add(slot);

            // Add the nodes to the slot ordered by defaultColor.
            while (queue.size() > 0) {
                slot.addNode(queue.poll());
            }
        }
        
    }
    
    int calcP_k(int color) {
        int p = 1;
        while (p < color) {
            p *= 2;
        }
        return p;
        
    }
}
