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
public class ExpoTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

//        SimpleDynamic.verbose = SimpleDynamic.Verbose.PRINT_QUEUE_LENGTHS;
//        LocalVoting.verbose = LocalVoting.Verbose.PRINT_QUEUE_LENGTHS;
//        LocalVoting.verbose = LocalVoting.Verbose.PRINT_SLOTS;
//        LocalVoting.verbose = LocalVoting.Verbose.PRINT_X;
//        LocalVoting.verbose.add(LocalVoting.Verbose.PRINT_SLOTS);
//        LocalVoting.verbose.add(LocalVoting.Verbose.PRINT_QUEUE_LENGTHS);
//        LocalVoting.verbose.add(LocalVoting.Verbose.PRINT_X);
//        LocalVoting.verbose.add(LocalVoting.Verbose.PRINT_SLOT_EXCHANGE);
        TDMAScheduller.print_queues = true;

        int nodes = 100;
        double transmissionRange = 10;
        double topologySize = 100;
        int slots = 10;
        double arrivalRate = 0.0001;
        Scenario.SchedulerType type = Scenario.SchedulerType.Lobats;
        Node.packet_loss = 0.0D;

//        Scenario scenario = new Scenario(10, 10, 50, 10, Scenario.SchedulerType.Balanced);
        Scenario scenario = new Scenario(nodes, transmissionRange, topologySize, slots, type);
        ExpoGenerator expoGen = new ExpoGenerator(scenario, arrivalRate, 0.001);
        scenario.network.stats = expoGen.stats;

//        Set<Connection> connectionSet = expoGen.conSet;
        scenario.simulator.run();

//        scenario.printStats();
//        for (Connection conn : connectionSet) {
//            System.out.println("Connection: " + conn.getTotalTime());
//            conn.dump();
//        }
        expoGen.stats.dump();
        expoGen.stats.dump_hist();

    }
}
