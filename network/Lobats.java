/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import simulator.Simulator;

/**
 *
 * @author dimitriv
 */
public class Lobats extends Lyui {

    Map<Node, ArrayList<Integer>> additional_colors = new HashMap<>();
    Map<Node, Double> lastIncr = new HashMap<>();
    int queueIncrLimit = 7;
    double NST = 50;

    public Lobats(Network network, Simulator simulator, double slotTime) {
        super(network, simulator, slotTime);

        for (Node node : network.nodes) {
            lastIncr.put(node, -NST);
            additional_colors.put(node, new ArrayList<Integer>());
        }
        sanityCheck();

    }

    @Override
    void nextSlot() {
        for (Node node : network.nodes) {
            if (node.backlog.size() > queueIncrLimit
                    && (simulator.now - lastIncr.get(node) > NST)) {
                findNewColor(node);
                lastIncr.put(node, simulator.now);

            } else if (node.backlog.size() == 0 && !additional_colors.get(node).isEmpty()) {
                removeExtraColors(node);
            }
        }

        super.nextSlot();
    }

    void removeExtraColors(Node node) {

        Set<Integer> colorsToRemove = new HashSet<>(additional_colors.get(node));
        additional_colors.put(node, new ArrayList<Integer>());
        for (int color : colorsToRemove) {
            int p = calcP_k(color);
            for (int t = 0; t < network.slots.size(); t++) {
                if (t % p == color % p) {
                    for (Reservation resv : new HashSet<>(network.slots.get(t).reservations)) {
                        if (resv.sender == node) {
                            resv.sender.reservations.remove(resv);
                            network.slots.get(t).reservations.remove(resv);
                        }
                    }
                }
            }
        }

        Set<Node> neighborhood = getOneTwoNeighbors(node);
        neighborhood.add(node);

        for (int color : colorsToRemove) {
            int p = calcP_k(color);
            for (int t = 0; t < network.slots.size(); t++) {
                if (t % p == color % p) {

                    final int slot = t;
                    PriorityQueue<Node> queue = new PriorityQueue<>(network.nodes.size(), new Comparator<Node>() {
                        @Override
                        public int compare(Node o1, Node o2) {
                            return -maxSuitableColor(o1, slot).compareTo(maxSuitableColor(o2, slot));
                        }
                    });

                    for (Node n : neighborhood) {
                        queue.offer(n);
                    }

                    while (queue.size() > 0) {
                        network.slots.get(t).addNode(queue.poll());
                    }
                }
            }
        }


    }

    void findNewColor(Node node) {
        nextcolor:
        for (int newcolor = 0; newcolor < network.slots.size(); newcolor++) {
            if (colorIsFree(node, newcolor)) {
                for (Node neighbor : node.neighbors) {
                    if (projectedUtilization(neighbor, newcolor) > 1) {
                        continue nextcolor;
                    }
                    for (Node twohop : neighbor.neighbors) {
                        if (twohop != node && projectedUtilization(twohop, newcolor) > 1) {
                            continue nextcolor;
                        }
                    }
                }

                addColor(node, newcolor);
                return;
            }
        }
    }

    void addColor(Node node, int newcolor) {
//        sanityCheck();
        additional_colors.get(node).add(newcolor);

        Set<Node> neighborhood = getOneTwoNeighbors(node);

        int p = calcP_k(newcolor);
        nextcolor:
        for (int t = 0; t < network.slots.size(); t++) {
            if (t % p == newcolor % p) {
                int max_suitable_color_in_neighborhood = newcolor;
                Node node_with_the_max_color = node;
                Set<Reservation> reservationsToBeRemoved = new HashSet<>();

                for (Reservation resv : network.slots.get(t).reservations) {
                    if (resv.sender == node) {
                        continue nextcolor;
                    }
                    if (neighborhood.contains(resv.sender)) {
                        if (maxSuitableColor(resv.sender, t) > max_suitable_color_in_neighborhood) {
                            node_with_the_max_color = resv.sender;
                            max_suitable_color_in_neighborhood = maxSuitableColor(node_with_the_max_color, t);
                        }
                        reservationsToBeRemoved.add(resv);
                    }
                }



                if (node_with_the_max_color == node) {
                    for (Reservation resv : reservationsToBeRemoved) {
                        resv.sender.reservations.remove(resv);
                        network.slots.get(t).reservations.remove(resv);
                    }
                    if (!network.slots.get(t).addNode(node)) {
                        System.err.println("Could not add slot but I should be able to");

                        network.slots.get(t).addNode(node);
                        System.exit(-1);
                    }
                }
            }
        }
    }

    final void sanityCheck() {

//        for (Slot slot : network.slots) {
//            for (Reservation resv : slot.reservations) {
//                
//            }
//        }

        for (int t = 0; t < network.slots.size(); t++) {
            for (Reservation resv : network.slots.get(t).reservations) {
                int mycolor = maxSuitableColor(resv.sender, t);

                if (mycolor < 0) {
                    System.err.println("Bad1");
                    System.exit(-1);
                }

                Set<Node> neighborhood = getOneTwoNeighbors(resv.sender);

                for (Node neighbor : neighborhood) {
                    if (maxSuitableColor(neighbor, t) > mycolor) {
//                        System.err.println("Bad2");
//                        System.exit(-1);
                    }
                }
            }
        }
        System.out.println("Things seem ok...");
    }

    Set<Node> getOneTwoNeighbors(Node node) {
        Set<Node> neighborhood = new HashSet<>();

        for (Node neighbor : node.neighbors) {
            neighborhood.add(neighbor);
            for (Node twohop : neighbor.neighbors) {
                if (twohop != node) {
                    neighborhood.add(twohop);
                }
            }
        }
        return neighborhood;
    }

    Integer maxSuitableColor(Node node, int t) {
        int result = -1;

        int p_k = calcP_k(defaultColor.get(node));

        if (t % p_k == defaultColor.get(node) % p_k) {
            result = defaultColor.get(node);
        }

        for (Integer color : additional_colors.get(node)) {
            p_k = calcP_k(color);
            if (t % p_k == color % p_k) {
                result = (result > color) ? result : color;
            }
        }
        return result;
    }

    boolean colorIsFree(Node node, int newcolor) {
        if (nodeHasColor(node, newcolor)) {
            return false;
        }
        for (Node neighbor : node.neighbors) {
            if (nodeHasColor(neighbor, newcolor)) {
                return false;
            }
            for (Node twoHop : neighbor.neighbors) {
                if (nodeHasColor(twoHop, newcolor)) {
                    return false;
                }
            }
        }
        return true;
    }

    boolean nodeHasColor(Node node, int color) {
        if (defaultColor.get(node) == color
                || additional_colors.get(node).contains(color)) {
            return true;
        } else {
            return false;
        }
    }

    double projectedUtilization(Node neighbor, int color) {

        int p = calcP_k(color);

        int lostSlots = 0;
        for (Reservation resv : neighbor.reservations) {
            int t = network.slots.indexOf(resv.slot);
            if (t % p == color % p) {
                if ((defaultColor.get(neighbor) < color)) {
                    lostSlots++;
                } else {
                    for (Integer neighborColor : additional_colors.get(neighbor)) {
                        if (neighborColor < color) {
                            lostSlots++;
                            break;
                        }
                    }
                }
            }
        }

        return (neighbor.utilization * neighbor.reservations.size()) / (neighbor.reservations.size()
                - lostSlots);
    }
}
