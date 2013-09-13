/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 *
 * @author bruker
 */
public class Node {

    Queue<Packet> backlog = new PriorityQueue<>();
    Map<Node, Node> routingtable;
    Map<Node, Double> distance;
    Set<Node> neighbors = new HashSet<>();
    Queue<Reservation> reservations = new PriorityQueue<>();
    double x;
    double y;
    int id;
    static int id_helper;
    double weight = 1;
    double utilization =0;
    public static double packet_loss = 0D;

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

    /** 
     * Obsolete 
     */
    void transmit_no_error() {
        
        double alpha_utilization = 0.1;
        
        if (!backlog.isEmpty()) {
            utilization = alpha_utilization+ (1-alpha_utilization)*utilization;
            
            Packet packet = backlog.poll();
            if (packet.dst == this) {
                System.err.println("This should not happen :(");
                System.exit(-1);
            }
            if (routingtable.containsKey(packet.dst)) {
//                System.out.println("Node " + this+ " forwarding packet to "
//                        + routingtable.get(packet.dst) + ", final destination "
//                        + packet.dst);
                routingtable.get(packet.dst).receive(packet);
            } else {
//                System.out.println("Can't find route to " + packet.dst
//                        + "... Dropping packet");
            }
        } else {
            utilization = (1-alpha_utilization)*utilization;
        }
    }
    
    
    void transmit() {
        
        double alpha_utilization = 0.1;
        
        if (!backlog.isEmpty()) {
            utilization = alpha_utilization+ (1-alpha_utilization)*utilization;
            
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
//                System.out.println("Node " + this+ " forwarding packet to "
//                        + routingtable.get(packet.dst) + ", final destination "
//                        + packet.dst);
                routingtable.get(packet.dst).receive(packet);
            } else {
//                System.out.println("Can't find route to " + packet.dst
//                        + "... Dropping packet");
            }
        } else {
            utilization = (1-alpha_utilization)*utilization;
        }
    }

    public void receive(Packet packet) {
        if (packet.dst == this) {
     //       System.out.println("Packet arrived to its destination at " + this);
            packet.connection.ack(packet);
        } else {
            backlog.add(packet);
        }
    }
    
    public Double getLoad() {
        return (1.0 * backlog.size()) / reservations.size();
    }
    
    public Double getX() {
        if (backlog.size() == 0) {
            System.err.println("X cannot be computed if backlog is zero");
            System.exit(-1);
        }
        return (reservations.size()*1D)/backlog.size();
    }
    
}
