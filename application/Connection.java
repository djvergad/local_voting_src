/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import simulator.Event;
import simulator.EventType;
import simulator.Handleable;
import simulator.Simulator;
import network.Node;
import network.Packet;

/**
 *
 * @author dimitriv
 */
public class Connection implements Handleable {

    Node source;
    Node destination;
    public Simulator simulator;
    int packets;
    double interval;
    int seq;
    static int conn_count;
    int id;
    double started;
    double stopped = Double.NaN ;
    Statistics stats;

    public Connection(Node source, Node destination, Simulator simulator, int packets, double interval, double startTime, Statistics stats) {
        this.source = source;
        this.destination = destination;
        this.simulator = simulator;
        this.packets = packets;
        this.interval = interval;
        simulator.offer(new Event(startTime,
                EventType.PacketGen, this));
        this.id = conn_count++;
        this.stats = stats;
    }

    @Override
    public void handle(Event event) {
        started = (started >0 )? started : event.time;
        switch (event.type) {
            case PacketGen: 
                source.receive(new Packet(source, destination, seq, this));
                if (++seq < packets) {
                    simulator.offer(new Event(simulator.now + interval,
                            EventType.PacketGen, this));
                }
                break;
            default:
                System.err.println("Wrong event type");
                System.exit(-1);
        }
    }

    public void ack(Packet packet) {
        stats.log(packet, simulator.now);
        
//        System.out.println(packet.sequenceNumber + "\t" + this.id + "\t" + packets);
        if (packet.sequenceNumber == packets - 1) {
//            System.out.println("time: " + simulator.now + " conn: " + this.id + " completetd");
            stopped = simulator.now;
        }
    }
    
    public double getTotalTime() {
        return (stopped - started);
    }
    
    public void dump() {
        System.out.println("Conn:\t" + id +"\tStarted:\t"+started+"\tpackets:\t"+packets);
    }
}
