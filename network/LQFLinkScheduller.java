/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import simulator.Event;
import simulator.EventType;
import simulator.Simulator;

/**
 *
 * @author dimitriv
 */
public class LQFLinkScheduller extends TDMAScheduller {

    public LQFLinkScheduller(Network network, Simulator simulator, double slotTime) {
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

        Set<Node> receivers = new HashSet<>();
        Set<Node> senders = new HashSet<>();
        Map<Node, Packet> packets = new HashMap<>();

        nextnode:
        while (pq.size() > 0) {
            Node src = pq.poll();

            for (Node n : receivers) {
                if (src.neighbors.contains(n)) {
                    continue nextnode;
                }
            }

            nextpacket:
            for (Packet pkt : src.backlog) {
                Node dst = src.routingtable.get(pkt.dst);
                for (Node n : senders) {
                    if (dst.neighbors.contains(n)) {
                        continue nextpacket;
                    }
                }
                
                senders.add(src);
                packets.put(src, pkt);
                receivers.add(dst);
            }
            
        }
        
        if (currentSlot == 0) {
            nextFrame();
        }


        for (Node src: senders) {
            src.transmit(packets.get(src));
        }
              
        currentSlot++;
        currentSlot = currentSlot % network.slots.size();
        simulator.offer(new Event(simulator.now + slotTime,
                EventType.NextSlot, this));

    }
}
