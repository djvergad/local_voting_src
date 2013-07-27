/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator;

import java.util.PriorityQueue;

/**
 *
 * @author dimitriv
 */
public class Simulator extends PriorityQueue<Event> implements Handleable {

    public double now;
    
    public void run() {
        while (!this.isEmpty()) {
            Event event = this.poll();
            now = event.time;
            event.handler.handle(event);
        }
    }
    
    @Override
    public void handle(Event event) {
        if (event.type == EventType.EndSimulation) {
//            System.out.println("Simulation is over!");
            this.removeAll(this);
        }
    }
}
