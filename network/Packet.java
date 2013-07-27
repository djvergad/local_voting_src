/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import application.TrafficGenerator;

/**
 *
 * @author bruker
 */
public class Packet implements Comparable<Packet>{
    public Node src;
    public Node dst;
    static int packetcount;
    Integer id;
    public int sequenceNumber;
    public TrafficGenerator trafficGenerator;

    public Packet(Node src, Node dst, int sequenceNumber, TrafficGenerator trafficGenerator) {
        this.src = src;
        this.dst = dst;
        this.trafficGenerator = trafficGenerator;
        this.sequenceNumber = sequenceNumber;
        this.id = packetcount++;
    }

    @Override
    public int compareTo(Packet o) {
        return this.id.compareTo(o.id);
    }
    
    
}
