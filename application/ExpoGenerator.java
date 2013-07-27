/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.util.HashSet;
import java.util.Set;
import network.Node;
import network.Scenario;
import simulator.Event;
import simulator.EventType;
import simulator.Handleable;
import simulator.Simulator;

/**
 *
 * @author dimitriv
 */
public class ExpoGenerator implements Handleable {

    Scenario scenario;
    double lamda;
    double mi;
    public Set<Connection> conSet = new HashSet<>();
    public Statistics stats = new Statistics();

    public ExpoGenerator(Scenario scenario, double lamda, double mi) {
        this.scenario = scenario;
        this.lamda = lamda;
        this.mi = mi;
                
        nextConnection();
    }

    final void nextConnection() {
        double interval = Math.log(Math.random()) / (-lamda);
        double duration = Math.log(Math.random()) / (-mi);
        int numpackets = (int) Math.round(duration / 5);
        Node src = scenario.network.nodes.get((int) (Math.random() * scenario.network.nodes.size()));
        Node dst = scenario.network.nodes.get((int) (Math.random() * scenario.network.nodes.size()));
        conSet.add(new Connection(src, dst, scenario.simulator, numpackets, 5 * scenario.scheduller.slotTime, scenario.simulator.now, stats));

        scenario.simulator.offer(new Event(scenario.simulator.now + interval, EventType.ConnectionArrival, this));
    }

    @Override
    public void handle(Event event) {
        switch (event.type) {
            case ConnectionArrival:
                nextConnection();
                break;
            default:
                System.err.println("Wrong event type!");
                System.exit(-1);
        }
    }
}
