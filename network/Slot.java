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
 * @author bruker
 */
public class Slot {

    Set<Reservation> reservations = new HashSet<>();
    
    public Slot() {
        
    }

    boolean checkNode(Node node) {
        for (Reservation reservation : reservations) {
            if (reservation.sender == node || reservation.blocked.contains(node)) {
                
                return false;
            }
        }
        return true;
    }

    boolean checkNode(Node node, Reservation toRemove) {
        for (Reservation reservation : reservations) {
            if (reservation == toRemove) {
                continue;
            }
            if (reservation.sender == node || reservation.blocked.contains(node)) {
                return false;
            }
        }
        return true;
    }

    boolean addNode(Node node) {
        if (!checkNode(node)) {
            return false;
        }

        Reservation reservation = new Reservation();
        reservation.sender = node;
        reservation.slot = this;

        for (Node neighbor : node.neighbors) {
            reservation.blocked.add(neighbor);
            for (Node hop2 : neighbor.neighbors) {
                if (hop2 != node) {
                    reservation.blocked.add(hop2);
                }
            }
        }
        reservations.add(reservation);
        node.reservations.add(reservation);
        return true;
    }

    @Override
    public String toString() {
//        String result="";
//        for (Reservation reservation : reservations) {
//            result+=reservation.toString();
//        }
//        return result;
        return Arrays.toString(reservations.toArray());

    }
}
