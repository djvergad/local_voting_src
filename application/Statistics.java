/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import network.Packet;
import network.Scenario;

/**
 *
 * @author dimitriv
 */
public class Statistics {

    class ConnStats {

        double starttime;
        double stoptime;
        int packets;
        double sumDelay;
        Connection connection;

        void log(Packet p, double time) {
            packets++;
            stoptime = time;
            sumDelay += time - p.timeSent;
        }

        double throughput() {
            if (stoptime != starttime) {
                return packets / (stoptime - starttime);
            } else {
                return Double.NaN;
            }
        }

        double delay() {
            return sumDelay / packets;
        }
    }
    Map<Connection, ConnStats> conStatMap = new HashMap<>();
    public static double starttime = 110000D / 3;
    double lastReceptionTime;
    int numPackets;
    double sumDelay;
    public Map<Integer, Integer> count_queue_lengths = new TreeMap<>();
    public Map<Long, Integer> count_nodal_delay = new TreeMap<>();
    public Map<Long, Integer> count_endtoend_delay = new TreeMap<>();
    public Map<Integer, Integer> count_slot_allocaations = new TreeMap<>();
    public Map<Double, Integer> count_load = new TreeMap<>();

    public void dump_hist() {
//        System.out.println("Dumping Queue:");
//        for (Entry<Integer, Integer> e : count_queue_lengths.entrySet()) {
//            System.out.printf("%d\t%d\n", e.getKey(), e.getValue());
//        }
//        System.out.println("Dumping Slots:");
//        for (Entry<Integer, Integer> e : count_slot_allocaations.entrySet()) {
//            System.out.printf("%d\t%d\n", e.getKey(), e.getValue());
//        }
//        System.out.println("Dumping Load:");
//        for (Entry<Double, Integer> e : count_load.entrySet()) {
//            System.out.printf("%f\t%d\n", e.getKey(), e.getValue());
//        }
        System.out.println("Dumping Endtoend:");
        for (Entry<Long, Integer> e : count_endtoend_delay.entrySet()) {
            System.out.printf("%d\t%d\n", e.getKey(), e.getValue());
        }
        System.out.println("Dumping nodal:");
        for (Entry<Long, Integer> e : count_nodal_delay.entrySet()) {
            System.out.printf("%d\t%d\n", e.getKey(), e.getValue());
        }
    }

//    public void log_histograms(int queue, int slots) {
//        add_to_int_map(count_queue_lengths, queue);
//        add_to_int_map(count_slot_allocaations, slots);
//        add_to_double_map(count_load, (1.0D * queue) / slots);
//        
//    }
    private void add_to_long_map(Map<Long, Integer> map, Long key) {
        int count = map.containsKey(key) ? map.get(key) : 0;
        map.put(key, count + 1);
    }

    private void add_to_double_map(Map<Double, Integer> map, Double key) {
        int count = map.containsKey(key) ? map.get(key) : 0;
        map.put(key, count + 1);
    }

    public void log_node(Packet packet, double timeReceived) {
        if (timeReceived > starttime) {
            add_to_long_map(count_nodal_delay, (long) Math.ceil(timeReceived - packet.timeEnQueued));
        }
    }

    public void log(Packet packet, double timeReceived) {
        if (timeReceived > starttime) {
            sumDelay += timeReceived - packet.timeSent;
            numPackets++;
            lastReceptionTime = timeReceived;
            if (!conStatMap.containsKey(packet.connection)) {
                ConnStats connStats = new ConnStats();
                connStats.starttime = timeReceived;
                connStats.connection = packet.connection;
                conStatMap.put(packet.connection, connStats);

            }
            conStatMap.get(packet.connection).log(packet, timeReceived);
            if ((long) Math.ceil(timeReceived - packet.timeSent) == 0) {
                System.err.println("This shouldn't happen! " + packet.connection.source + "\t" + packet.connection.destination + "\t" + packet.timeSent + "\t" + packet.connection.simulator.now);
                System.exit(-1);
            }
            add_to_long_map(count_endtoend_delay, (long) Math.ceil(timeReceived - packet.timeSent));
        }
    }

    public void dump() {
        System.out.print("STATSDUMP\tThroughput:\t" + numPackets
                / (lastReceptionTime - starttime)
                + "\tDelay:\t" + sumDelay / numPackets);

        double sumth = 0D, sumth2 = 0D, sumdel = 0D, sumdel2 = 0D, sumdt = 0D, sumdt2 = 0D;
        int countth = 0, countdel = 0;
        for (ConnStats connStats : conStatMap.values()) {
            if (!Double.isNaN(connStats.throughput())) {
//                System.out.println(connStats.throughput());
                countth++;
                sumth += connStats.throughput();
                sumth2 += Math.pow(connStats.throughput(), 2);
            }
            countdel++;
            sumdel += connStats.delay();
            sumdel2 += Math.pow(connStats.delay(), 2);

            sumdt += connStats.stoptime - connStats.connection.started;
            sumdt2 += Math.pow(connStats.stoptime - connStats.connection.started, 2);
//            System.out.println("throughput:\t" + connStats.throughput()+
//                    "\tdelay:\t"+connStats.delay());

        }
        System.out.println("\tDeliveryTime:\t" + sumdt / countdel + "\tThFair:\t" + Math.pow(sumth, 2) / (countth * sumth2)
                + "\tDelFair:\t" + Math.pow(sumdel, 2) / (countdel * sumdel2) + "\tFairDelivery:\t"
                + Math.pow(sumdt, 2) / (countdel * sumdt2));
    }
}
