/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stability.network;

import java.util.ArrayList;

/**
 *
 * @author bruker
 */
public class Slot {

    ArrayList<Node> transmitters;
    ArrayList<Node> receivers;
    ArrayList<Node> blocked;

    boolean checkNode(Node node) {
        return !(transmitters.contains(node) || receivers.contains(node)
                || blocked.contains(node));
    }
}
