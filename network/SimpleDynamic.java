/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import application.Statistics;
import simulator.Simulator;

/**
 *
 * @author dimitriv
 */
public class SimpleDynamic extends TDMAScheduller {

    public enum Verbose {

        NO,
        PRINT_QUEUE_LENGTHS,
        PRINT_W
    }
    public static Verbose verbose;

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
                } else if (verbose == verbose.PRINT_W && simulator.now > Statistics.starttime) {
                    System.out.println(node.id + "\t1");
                }
            } else if (node.backlog.isEmpty() && node.reservations.size() > 0) {
                node.removeSlot();
                if (verbose == verbose.PRINT_W && simulator.now > Statistics.starttime) {
                    System.out.println(node.id + "\t-1");
                }
            }
        }

        if (verbose == Verbose.PRINT_QUEUE_LENGTHS) {
            System.out.print(simulator.now + "\t");
            for (Node node : network.nodes) {
                System.out.print(node.backlog.size() + "\t");
            }
            System.out.println();
        }

//        simulator.offer(new Event(simulator.now + network.slots.size()
//                * slotTime, EventType.NextFrame, this));
    }

    public void loadBalance(Node node) {
    }
}
