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
public class Scenario {

    Network network;
    double transmissionRange;
    double topologySize;
    int nodes;
    int slots;

    public Scenario(int nodes, double transmissionRange, double topologySize, int slots) {
        this.nodes = nodes;
        this.transmissionRange = transmissionRange;
        this.topologySize = topologySize;
        this.slots = slots;

        network = new Network();
        network.slots = new ArrayList<>();
        for (int i = 0; i < slots; i++) {
            network.slots.add(new Slot());
        }

        for (int i = 0; i < nodes; i++) {
            Node node = new Node();
            node.x = Math.random() * topologySize;
            node.y = Math.random() * topologySize;
            network.nodes.add(node);
        }
        createTopology();
        
        network.dump();
        network.updateRoutes();
    }
    
    final void createTopology() {
        for (Node node1 : network.nodes) {
            for (Node node2 : network.nodes) {
                if (node1 != node2 && distance(node1, node2) < transmissionRange) {
                    node1.neighbors.add(node2);
                    node2.neighbors.add(node1);
                }
            }
        }
    }
    
    double distance(Node node1, Node node2) {
        double dx = node1.x - node2.x;
        double dy = node2.y - node2.y;
        return Math.sqrt(dx*dx + dy*dy );
    }
}
