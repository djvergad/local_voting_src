/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.util.HashSet;
import java.util.Set;
import network.Node;
import network.Scenario;

/**
 *
 * @author dimitriv
 */
public class StartGenerator {
    
    public static Statistics stats = new Statistics();

    public static Set<Connection> generate(int connections, Scenario scenario) {
        Set<Connection> trafficGenerators = new HashSet<>();
        for (int i = 0; i < connections; i++) {
            Node src = scenario.network.nodes.get((int) (Math.random() * scenario.network.nodes.size()));
            Node dst = scenario.network.nodes.get((int) (Math.random() * scenario.network.nodes.size()));
            System.out.println(scenario.network.nodes.size() + " " + src + " " + dst);
            trafficGenerators.add(new Connection(src, dst, scenario.simulator, 100, 5 *  scenario.scheduller.slotTime, 1.0, stats));
        }
        return trafficGenerators;
    }
}
