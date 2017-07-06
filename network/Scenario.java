/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import application.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import simulator.Event;
import simulator.EventType;
import simulator.Simulator;

/**
 *
 * @author bruker
 */
public class Scenario {

    public Network network;
    double transmissionRange;
    double topologySize;
    int nodes;
    int slots;
//    public Set<TrafficGenerator> trafficGenerators = new HashSet<>();
    public Simulator simulator;
    public TDMAScheduller scheduller;

    public enum SchedulerType {

        Simple,
        Balanced,
        LQF,
        DRAND,
        Lyui,
        Lobats,
        LocalVoting,
        LQFLinkScheduller
    }

    public Scenario(int nodes, double transmissionRange, double topologySize, int slots, SchedulerType type) {
        this.nodes = nodes;
        this.transmissionRange = transmissionRange;
        this.topologySize = topologySize;
        this.slots = slots;

        network = new Network();
        network.slots = new ArrayList<>();
        for (int i = 0; i < slots; i++) {
            network.slots.add(new Slot());
        }

        do {
            network.nodes = new ArrayList<>();
            Node.id_helper = 0;

            for (int i = 0; i < nodes; i++) {
                Node node = new Node();
                node.x = Math.random() * topologySize;
                node.y = Math.random() * topologySize;
                network.nodes.add(node);
            }
            createTopology();

            //network.dump();
            network.updateRoutes();
        } while (!network.isConnected());


        simulator = new Simulator();
        
        switch (type) {
            case Simple:
                scheduller = new SimpleDynamic(network, simulator, 1.0);
                break;
            case Balanced:
                scheduller = new LoadScheduller(network, simulator, 1.0);
                break;
            case LQF:
                scheduller = new LQFScheduller(network, simulator, 1.0);
                break;
            case DRAND:
                scheduller = new DRAND(network, simulator, 1.0);
                break;
            case Lyui:
                scheduller = new Lyui(network, simulator, 1.0);
                break;
            case Lobats:
                scheduller = new Lobats(network, simulator, 1.0);
                break;
            case LocalVoting:
                scheduller = new LocalVoting(network, simulator, 1.0);
                break;
            case LQFLinkScheduller:
                scheduller = new LQFLinkScheduller(network, simulator, 1.0);
                break;
            default:
                System.err.println("Wrong Type");
                scheduller = null;
                System.exit(-1);
        }



        simulator.offer(new Event(300000, EventType.EndSimulation, simulator));

//        for (int i = 0; i < connections; i++) {
//            Node src = network.nodes.get((int) (Math.random() * network.nodes.size()));
//            Node dst = network.nodes.get((int) (Math.random() * network.nodes.size()));
//            System.out.println(network.nodes.size() + " " + src + " " + dst);
//            trafficGenerators.add(new Connection(src, dst, simulator, 100, 5 * scheduller.slotTime, 1.0));
//        }


        //network.nodes.get(0).receive(new Packet(network.nodes.get(0), network.nodes.get(network.nodes.size() - 1)));
//        trafficGenerators.add(new Connection(network.nodes.get(0), network.nodes.get(network.nodes.size() - 1), simulator, 100, 5 * scheduller.slotTime, 1.0));
//        trafficGenerators.add(new Connection(network.nodes.get(1), network.nodes.get(network.nodes.size() - 2), simulator, 100, 5 * scheduller.slotTime, 1.0));
//        trafficGenerators.add(new Connection(network.nodes.get(2), network.nodes.get(network.nodes.size() - 3), simulator, 100, 5 * scheduller.slotTime, 1.0));
//        trafficGenerators.add(new Connection(network.nodes.get(3), network.nodes.get(network.nodes.size() - 4), simulator, 100, 5 * scheduller.slotTime, 1.0));
//        trafficGenerators.add(new Connection(network.nodes.get(4), network.nodes.get(network.nodes.size() - 5), simulator, 100, 5 * scheduller.slotTime, 1.0));
//        simulator.run();

    }

    final void createTopology() {
        for (Node node1 : network.nodes) {
            for (Node node2 : network.nodes) {
                if (node1 != node2 && distance(node1, node2) < transmissionRange) {
                    node1.neighbors.add(node2);
                    node2.neighbors.add(node1);
                }
            }
        }
    }

    double distance(Node node1, Node node2) {
        double dx = node1.x - node2.x;
        double dy = node2.y - node2.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public void printStats() {
        double sumOne = 0;
        double sumTwo = 0;
        int count = 0;
        double maxDistance = 0D;
        for (Node node : network.nodes) {
            int oneHop = node.neighbors.size();
            Set<Node> twoHop = new HashSet<>();
            for (Node neighbor : node.neighbors) {
                for (Node sec : neighbor.neighbors) {
                    if (sec != node && !node.neighbors.contains(sec)) {
                        twoHop.add(sec);
                    }
                }
            }
//            System.out.println("One:\t"+oneHop+"\tTwoHop\t"+twoHop.size());
            count++;
            sumOne += oneHop;
            sumTwo += twoHop.size();

            for (Node othernode : network.nodes) {
                maxDistance = Math.max(node.distance.get(othernode), maxDistance);
                //System.out.println(node.distance.get(othernode));
            }
        }
        System.out.println("Average: One:\t" + sumOne / count + "\tTwoHop\t" + sumTwo / count + "\tdiameter\t" + maxDistance);
    }
}
