/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author dimitriv
 */
public class Reservation implements Comparable<Reservation> {
    Node sender;
    Set<Node> blocked = new HashSet<>();
    Slot slot;
    
    @Override
    public String toString () {
        String result = "Sender: " + sender+" blocked: ";
        result += Arrays.toString(blocked.toArray());
        return result;
    }

    @Override
    public int compareTo(Reservation o) {
        return Integer.valueOf(this.blocked.size()).compareTo(o.blocked.size());
    }
   
}
