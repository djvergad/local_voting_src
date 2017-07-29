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
            int id_src = (int) (Math.random() * scenario.network.nodes.size());
            int id_dst = (int) (Math.random() * (scenario.network.nodes.size()-1));
            if (id_dst>=id_src) {
                id_dst++;
            }
            Node src = scenario.network.nodes.get(id_src);
            Node dst = scenario.network.nodes.get(id_dst);
//            System.out.println(scenario.network.nodes.size() + " " + src + " " + dst);
            trafficGenerators.add(new Connection(src, dst, scenario.simulator, 100, 5 *  scenario.scheduller.slotTime, 1.0, stats));
        }
        return trafficGenerators;
    }
}
