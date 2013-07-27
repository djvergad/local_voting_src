/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import simulator.Event;
import simulator.EventType;
import simulator.Simulator;

/**
 *
 * @author dimitriv
 */
public class SimpleDynamic extends TDMAScheduller {

    public SimpleDynamic(Network network, Simulator simulator, double slotTime) {
        super(network, simulator, slotTime);
    }

    @Override
    void nextFrame() {
        currentFrame++;
//        currentSlot = 0;
        for (Node node : network.nodes) {
            if (!node.backlog.isEmpty()) {
                if (!network.findSlot(node)) {
                    loadBalance(node);
//                    System.out.println("No slots available!!!");
                }
            } else if (node.backlog.isEmpty() && node.reservations.size() > 0) {
                node.removeSlot();
            }
        }

//        simulator.offer(new Event(simulator.now + network.slots.size()
//                * slotTime, EventType.NextFrame, this));
    }

    public void loadBalance(Node node) {
    }
}
