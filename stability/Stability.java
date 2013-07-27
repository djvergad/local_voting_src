/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stability;

import application.TrafficGenerator;
import network.Scenario;

/**
 *
 * @author bruker
 */
public class Stability {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //Scenario scenario = new Scenario(100, 10, 100, 10);
        
        int nodes = Integer.valueOf(args[0]);
        double transmissionRange = Double.valueOf(args[1]);
        double topologySize = Double.valueOf(args[2]);
        int slots = Integer.valueOf(args[3]);
        int connections = Integer.valueOf(args[4]);
        Scenario.SchedulerType type = Scenario.SchedulerType.valueOf(args[5]);
        
//        Scenario scenario = new Scenario(10, 10, 50, 10, Scenario.SchedulerType.Balanced);
        Scenario scenario = new Scenario(nodes, transmissionRange, topologySize, slots, connections, type);
        
        for (TrafficGenerator tg : scenario.trafficGenerators) {
            System.out.println("Connection: " +tg.getTotalTime());
        }
        
    }
}
