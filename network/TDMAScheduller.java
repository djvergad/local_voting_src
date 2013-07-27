/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import simulator.Event;
import simulator.EventType;
import simulator.Handleable;
import simulator.Simulator;

/**
 *
 * @author dimitriv
 */
public class TDMAScheduller implements Handleable {

    Network network;
    Simulator simulator;
    double slotTime;
    int currentSlot;
    int currentFrame;

    public TDMAScheduller(Network network, Simulator simulator,
            double slotTime) {
        this.network = network;
        this.simulator = simulator;
        this.slotTime = slotTime;

//        simulator.offer(new Event(simulator.now, EventType.NextFrame, this));
        simulator.offer(new Event(simulator.now, EventType.NextSlot, this));

    }

    void nextSlot() {
        if (currentSlot == 0) {
            nextFrame();
        }
        
        
//        System.out.println(simulator.now + " Slot " + currentSlot);
        for (Reservation reservation : network.slots.get(currentSlot).reservations) {
            reservation.sender.transmit();
        }
        currentSlot++;
        currentSlot = currentSlot % network.slots.size();
        simulator.offer(new Event(simulator.now + slotTime,
                EventType.NextSlot, this));
    }

    void nextFrame() {
//        System.out.println(simulator.now + " Frame " + currentFrame);
        currentFrame++;
//        currentSlot = 0;

//        simulator.offer(new Event(simulator.now + network.slots.size()
//                * slotTime, EventType.NextFrame, this));
    }

    @Override
    public void handle(Event event) {
        switch (event.type) {
            case NextSlot:
                nextSlot();
                break;
            default:
                System.err.println("Wrong event type");
                System.exit(-1);
        }
    }
}
