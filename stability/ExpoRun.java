/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stability;

import application.ExpoGenerator;
import network.Node;
import network.Scenario;
import network.TDMAScheduller;

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
        Node.packet_loss = Double.valueOf(args[6]);
        
        System.out.println("we have " + args.length + " args and the value is " +Boolean.valueOf(args[8]));
        if (args.length > 8 && Boolean.valueOf(args[8])) {
            TDMAScheduller.print_queues = true;
        }

//        Scenario scenario = new Scenario(10, 10, 50, 10, Scenario.SchedulerType.Balanced);
        Scenario scenario = new Scenario(nodes, transmissionRange, topologySize, slots, type);        
        ExpoGenerator expoGen = new ExpoGenerator(scenario, arrivalRate, 0.001);
        scenario.network.stats = expoGen.stats;
        scenario.simulator.run();
        expoGen.stats.dump();

        if (args.length > 8 && Boolean.valueOf(args[8])) {
            expoGen.stats.dump_hist();
        }
    }
}
