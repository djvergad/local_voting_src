/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.util.Comparator;
import java.util.PriorityQueue;
import simulator.Simulator;

/**
 *
 * @author dimitriv
 */
public class LQFScheduller extends TDMAScheduller {

    public LQFScheduller(Network network, Simulator simulator, double slotTime) {
        super(network, simulator, slotTime);
    }

    @Override
    void nextSlot() {

        // pq is sorted based on the node's queue sizes
        PriorityQueue<Node> pq = new PriorityQueue<>(network.nodes.size(), new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                if (o1.backlog.size() == o2.backlog.size()) {
                    return -Integer.compare(o1.neighbors.size(), o2.neighbors.size());
                }
                return -Integer.compare(o1.backlog.size(), o2.backlog.size());
            }
        });

        for (Node node : network.nodes) {
            if (node.backlog.size() > 0) {
                pq.add(node);
            }
        }
//        pq.addAll(network.nodes);

        Slot slot = network.slots.get(currentSlot);
        while (pq.size() > 0) {
            Node node = pq.poll();
//            System.out.println(node.backlog.size());
            slot.addNode(node);
        }


        super.nextSlot();
        
        // Now remove the reservations
        for (Node node : network.nodes) {
            if (!node.reservations.isEmpty()) {
                node.removeSlot();
            }
        }
    }
}
