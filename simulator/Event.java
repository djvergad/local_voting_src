/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator;


/**
 *
 * @author dimitriv
 */
public class Event implements Comparable<Event>{
    public double time;
    public EventType type;
    public Handleable handler;

    public Event(double time, EventType type, Handleable handler) {
        this.time = time;
        this.type = type;
        this.handler = handler;
    }

    @Override
    public int compareTo(Event o) {
        return Double.valueOf(this.time).compareTo(o.time);
    }
    
}

