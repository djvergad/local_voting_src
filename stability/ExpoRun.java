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

/**
 *
 * @author bruker
 */
public class ExpoRun {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

       
        int nodes = Integer.valueOf(args[0]);
        double transmissionRange = Double.valueOf(args[1]);
        double topologySize = Double.valueOf(args[2]);
        int slots = Integer.valueOf(args[3]);
        double arrivalRate = Double.valueOf(args[4]);
        Scenario.SchedulerType type = Scenario.SchedulerType.valueOf(args[5]);
        
//        Scenario scenario = new Scenario(10, 10, 50, 10, Scenario.SchedulerType.Balanced);
        Scenario scenario = new Scenario(nodes, transmissionRange, topologySize, slots, type);        
        ExpoGenerator expoGen = new ExpoGenerator(scenario, arrivalRate, 0.01);

        Set<Connection> connectionSet = expoGen.conSet;
        scenario.simulator.run();

//        scenario.printStats();
//        for (Connection conn : connectionSet) {
//            System.out.println("Connection: " + conn.getTotalTime());
//            conn.dump();
//        }

        expoGen.stats.dump();

    }
}
