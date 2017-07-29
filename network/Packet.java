/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import application.Connection;

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
    public Connection connection;
    public double timeSent;
    public double timeEnQueued;

    public Packet(Node src, Node dst, int sequenceNumber, Connection trafficGenerator) {
        this.src = src;
        this.dst = dst;
        this.connection = trafficGenerator;
        this.sequenceNumber = sequenceNumber;
        this.id = packetcount++;
        this.timeSent = trafficGenerator.simulator.now;
    }

    @Override
    public int compareTo(Packet o) {
        return this.id.compareTo(o.id);
    }
    
    public void mark_queue_time() {
        this.timeEnQueued = connection.simulator.now;
    }
    
}
