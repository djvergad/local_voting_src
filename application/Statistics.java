/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.util.HashMap;
import java.util.Map;
import network.Packet;

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
    double starttime = 110000D / 3;
    double lastReceptionTime;
    int numPackets;
    double sumDelay;

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
