/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stability;

import application.StartGenerator;
import application.Connection;
import java.util.Set;
import network.Scenario;

/**
 *
 * @author bruker
 */
public class StabilityTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //Scenario scenario = new Scenario(100, 10, 100, 10);
        
        int nodes = 100;
        double transmissionRange = 10;
        double topologySize = 100;
        int slots = 10;
//        int connections = 30;
        int connections = 2;
         Scenario.SchedulerType type = Scenario.SchedulerType.LQF;
   //    Scenario.SchedulerType type = Scenario.SchedulerType.Lobats;

//        Scenario scenario = new Scenario(10, 10, 50, 10, Scenario.SchedulerType.Balanced);
        Scenario scenario = new Scenario(nodes, transmissionRange, topologySize, slots, type);
        Set<Connection> trafficGenerators = StartGenerator.generate(connections, scenario);
        scenario.simulator.run();
        
        scenario.printStats();
        for (Connection tg : trafficGenerators) {
            System.out.println("Connection: " +tg.getTotalTime());
        }
        
    }
}
