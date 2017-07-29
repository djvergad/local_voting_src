/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 *
 * @author bruker
 */
public class Node {

    Queue<Packet> backlog = new LinkedList<>();
    Map<Node, Node> routingtable;
    Map<Node, Double> distance;
    Set<Node> neighbors = new HashSet<>();
    Queue<Reservation> reservations = new PriorityQueue<>();
    double x;
    double y;
    int id;
    static int id_helper;
    double weight = 1;
    double utilization = 0;
    public static double packet_loss = 0D;
    Double x_1 = 0D, x_2 = 0D;

    public Node() {
        id = id_helper++;
    }

    public void dump() {
        System.out.println("Node: " + id
                + " X= " + x
                + " Y= " + y
                + " Neighbors: " + Arrays.toString(neighbors.toArray()));
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }

    void removeSlot() {
        Reservation reservation = reservations.poll();
        reservation.slot.reservations.remove(reservation);
    }

    void transmit() {

        double alpha_utilization = 0.1;

        if (!backlog.isEmpty()) {
            utilization = alpha_utilization + (1 - alpha_utilization) * utilization;

            // If we have packet loss nothing happens
            if (Math.random() < packet_loss) {
                return;
            }

            Packet packet = backlog.poll();
            if (packet.dst == this) {
                System.err.println("This should not happen :(");
                System.exit(-1);
            }
            if (routingtable.containsKey(packet.dst)) {
                routingtable.get(packet.dst).receive(packet);
            } else {
//                System.out.println("Can't find route to " + packet.dst
//                        + "... Dropping packet");
            }
        } else {
            utilization = (1 - alpha_utilization) * utilization;
        }
    }

    void transmit(Packet packet) {

        double alpha_utilization = 0.1;

        if (!backlog.isEmpty() && backlog.contains(packet)) {
            utilization = alpha_utilization + (1 - alpha_utilization) * utilization;

            // If we have packet loss nothing happens
            if (Math.random() < packet_loss) {
                return;
            }

            backlog.remove(packet);
            
            if (packet.dst == this) {
                System.err.println("This should not happen :(");
                System.exit(-1);
            }
            if (routingtable.containsKey(packet.dst)) {
                routingtable.get(packet.dst).receive(packet);
            } else {
//                System.out.println("Can't find route to " + packet.dst
//                        + "... Dropping packet");
            }
        } else {
            utilization = (1 - alpha_utilization) * utilization;
        }
    }

    public void receive(Packet packet) {

        if (packet.src != this) {
            packet.connection.stats.log_node(packet, packet.connection.simulator.now);
        }
        
        if (packet.dst == this) {
            packet.connection.ack(packet);
        } else {
            packet.mark_queue_time();
            backlog.add(packet);
        }
    }

    public Double getLoad() {
        return (1.0 * backlog.size()) / reservations.size();
    }

    public Double getX() {
        return (backlog.size() * 1D) / reservations.size() ;
    }

    public void newFrame() {
        x_2 = x_1;
        x_1 = getX();
    }

    public Double getX_1() {
        return x_1;
    }

    public Double getX_2() {
        return x_2;
    }
}
