/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stability;

import application.TrafficGenerator;
import network.Lyui;
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
        int connections = 0;
         Scenario.SchedulerType type = Scenario.SchedulerType.LQF;
   //    Scenario.SchedulerType type = Scenario.SchedulerType.Lobats;

//        Scenario scenario = new Scenario(10, 10, 50, 10, Scenario.SchedulerType.Balanced);
        Scenario scenario = new Scenario(nodes, transmissionRange, topologySize, slots, connections, type);
        
        scenario.printStats();
        for (TrafficGenerator tg : scenario.trafficGenerators) {
            System.out.println("Connection: " +tg.getTotalTime());
        }
        
    }
}
