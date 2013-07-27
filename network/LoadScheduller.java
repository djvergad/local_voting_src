/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import simulator.Simulator;

/**
 *
 * @author dimitriv
 */
public class LoadScheduller extends SimpleDynamic {

    public LoadScheduller(Network network, Simulator simulator, double slotTime) {
        super(network, simulator, slotTime);
    }

    @Override
    public void loadBalance(Node node) {

        double minLoad = Double.MAX_VALUE;
        Reservation toRemove = null;
        for (Node neighbor : node.neighbors) {
            if (neighbor.reservations.size() > 0) {
                double load = neighbor.getLoad();
                if (load < minLoad) {
                    Reservation tmp = getSlot(node, neighbor);
                    if (tmp != null) {
                        toRemove = tmp;
                        minLoad = load;
                    }
                }
            }
        }
        if (minLoad < node.getLoad() && toRemove != null ) {
        
            toRemove.sender.reservations.remove(toRemove);
            toRemove.slot.reservations.remove(toRemove);
            if (!toRemove.slot.addNode(node)) {
                System.out.println("Something bad happened");
                System.exit(-1);
            }
        } else {
            System.out.println("No slots available - sorry!");
        }
    }

    public Reservation getSlot(Node node, Node neighbor) {
        for (Reservation res : neighbor.reservations) {
            if (res.blocked.contains(node)) {
                if (res.slot.checkNode(node, res)) {
                    return res;
                }
            }
        }
        return null;
    }
}
