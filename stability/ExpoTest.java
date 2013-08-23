/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stability;

import application.StartGenerator;
import application.Connection;
import application.ExpoGenerator;
import java.util.Set;
import network.Scenario;
import network.SimpleDynamic;

/**
 *
 * @author bruker
 */
public class ExpoTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        SimpleDynamic.verbose = SimpleDynamic.Verbose.PRINT_W;

        int nodes = 100;
        double transmissionRange = 10;
        double topologySize = 100;
        int slots = 10;
        double arrivalRate = 0.001;
        Scenario.SchedulerType type = Scenario.SchedulerType.Simple;

//        Scenario scenario = new Scenario(10, 10, 50, 10, Scenario.SchedulerType.Balanced);
        Scenario scenario = new Scenario(nodes, transmissionRange, topologySize, slots, type);
        ExpoGenerator expoGen = new ExpoGenerator(scenario, arrivalRate, 0.001);

        Set<Connection> connectionSet = expoGen.conSet;
        scenario.simulator.run();

//        scenario.printStats();
//        for (Connection conn : connectionSet) {
//            System.out.println("Connection: " + conn.getTotalTime());
//            conn.dump();
//        }

//        expoGen.stats.dump();

    }
}
