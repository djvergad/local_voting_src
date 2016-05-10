/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stability;

import application.Connection;
import application.ExpoGenerator;
import java.util.Set;
import network.LocalVoting;
import network.Node;
import network.Scenario;

/**
 *
 * @author bruker
 */
public class GammaTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

//        SimpleDynamic.verbose = SimpleDynamic.Verbose.PRINT_QUEUE_LENGTHS;
//        LocalVoting.verbose = LocalVoting.Verbose.PRINT_QUEUE_LENGTHS;
//        LocalVoting.verbose = LocalVoting.Verbose.PRINT_SLOTS;
//        LocalVoting.verbose = LocalVoting.Verbose.PRINT_X;

        int nodes = 100;
        double transmissionRange = 10;
        double topologySize = 100;
        int slots = 10;
        double arrivalRate = 0.001;
//        Scenario.SchedulerType type = Scenario.SchedulerType.Simple;
        Scenario.SchedulerType type = Scenario.SchedulerType.LocalVoting;
        Node.packet_loss = 0.0D;

//        Scenario scenario = new Scenario(10, 10, 50, 10, Scenario.SchedulerType.Balanced);

        for (LocalVoting.gamma = 0.001; LocalVoting.gamma <= 1000; LocalVoting.gamma *= 10) {

            for (int i = 0; i < 10; i++) {

                Scenario scenario = new Scenario(nodes, transmissionRange, topologySize, slots, type);
                ExpoGenerator expoGen = new ExpoGenerator(scenario, arrivalRate, 0.001);

                Set<Connection> connectionSet = expoGen.conSet;
                scenario.simulator.run();

//        scenario.printStats();
//        for (Connection conn : connectionSet) {
//            System.out.println("Connection: " + conn.getTotalTime());
//            conn.dump();
//        }

                System.out.print("gamma=\t" +LocalVoting.gamma + "\ti=\t"+i+"\t");
                expoGen.stats.dump();
            }
        }

    }
}
