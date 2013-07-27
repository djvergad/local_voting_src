/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import simulator.Simulator;

/**
 *
 * @author dimitriv
 */
public class DRAND extends TDMAScheduller {

    public DRAND(Network network, Simulator simulator, double slotTime) {
        super(network, simulator, slotTime);

        createSchedulle();

        network.dumpSlots();


    }

    final void createSchedulle() {
        network.slots.clear();
        ArrayList<Node> unSchedulled = new ArrayList<>(network.nodes);
        Slot slot = new Slot();

        do {
            // Create C_j
            int[] c_j = new int[network.nodes.size()];

            for (Node node : network.nodes) {
                c_j[node.id] = 0;
                Set<Node> checked = new HashSet<>();
                for (Node oneHop : node.neighbors) {
                    checked.add(oneHop);
                    if (unSchedulled.contains(oneHop)) {
                        c_j[node.id]++;
                    //    System.out.println("Node: " + node + " oneHop: " + oneHop + " c_j:" + c_j[node.id]);
                    }
                    for (Node twoHop : oneHop.neighbors) {
                        if (twoHop != node && !node.neighbors.contains(twoHop) && !checked.contains(twoHop)) {
                            checked.add(twoHop);
                            if (unSchedulled.contains(twoHop)) {
                                c_j[node.id]++;
                      //          System.out.println("Node: " + node + " oneHop: " + oneHop + " twoHop: " + twoHop + " c_j:" + c_j[node.id]);
                            }
                        }
                    }
                }
            }

            Set<Node> toRemove = new HashSet<>();
            for (Node node : unSchedulled) {

                double max_k = 0;

                for (Node oneHop : node.neighbors) {
                    max_k = max_k > c_j[oneHop.id] ? max_k : c_j[oneHop.id];

                    for (Node twoHop : oneHop.neighbors) {
                        if (twoHop != node && !node.neighbors.contains(twoHop)) {
                            max_k = max_k > c_j[twoHop.id] ? max_k : c_j[twoHop.id];
                        }
                    }
                }
               // System.out.println(max_k);

                // If he won the lottery
                if (max_k > 0 && Math.random() < 1.0 / max_k) {
                    // try to add the node
                    if (slot.addNode(node)) {
                        System.out.println("Added node " + node + " to slot " + slot);
                        toRemove.add(node);

                    }
                }
            }

            unSchedulled.removeAll(toRemove);
            if (slot.reservations.size() > 0) {
                network.slots.add(slot);
                slot = new Slot();
            }
        } while (unSchedulled.size() > 0);

    }
}
