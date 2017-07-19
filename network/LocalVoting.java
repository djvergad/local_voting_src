/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import application.Statistics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import simulator.Simulator;

/**
 *
 * @author dimitriv
 */
public class LocalVoting extends TDMAScheduller {

    public enum Verbose {

        NO,
        PRINT_QUEUE_LENGTHS,
        PRINT_W,
        PRINT_SLOT_EXCHANGE,
        PRINT_SLOTS,
        PRINT_X,
        PRINT_DMAX
    }
    public static Set<Verbose> verbose = new HashSet<>();
    public static double gamma = 50D;

    public LocalVoting(Network network, Simulator simulator, double slotTime) {
        super(network, simulator, slotTime);
    }

    @Override
    void nextFrame() {
        currentFrame++;
//        currentSlot = 0;
        for (Node node : network.nodes) {
// Remove only one slot instead of all of them         
            if (node.backlog.isEmpty()) {
//                while (node.reservations.size() > 0) {
                if (node.reservations.size() > 0) {
                    node.removeSlot();
                    if (verbose.contains(Verbose.PRINT_W) && simulator.now > Statistics.starttime) {
                        System.out.println(node.id + "\t-1");
                    }
                }
            }
        }

        Node[] nodes_sorted = network.nodes.toArray(new Node[0]);
        // Collections.shuffle(nodes_shuffled);
        Arrays.sort(nodes_sorted, (n1, n2) -> -n1.getX().compareTo(n2.getX()));

        Map<Node, Integer> to_add = new HashMap<>();
        for (Node node : nodes_sorted) {
            if (node.backlog.size() > node.reservations.size()) {
                to_add.put(node, node.backlog.size() - node.reservations.size());
            }
        }

        while (to_add.size() > 0) {
            for (Node node : nodes_sorted) {
                if (to_add.get(node) == null) {
                    continue;
                }
                if (!network.findSlot(node)) {
                    to_add.remove(node);
                } else {
                    int rem = to_add.get(node) - 1;
                    if (rem <= 0) {
                        to_add.remove(node);
                    }
                    if (verbose.contains(Verbose.PRINT_W) && simulator.now > Statistics.starttime) {
                        System.out.println(node.id + "\t1");
                    }
                }
            }
        }

        loadBalance(nodes_sorted);

        if (verbose.contains(Verbose.PRINT_QUEUE_LENGTHS)) {
            System.out.print(simulator.now + "\tqueue\t");
            for (Node node : network.nodes) {
                System.out.print(node.backlog.size() + ":" + node.reservations.size() + "\t");
            }
            System.out.println();
        }
        if (verbose.contains(Verbose.PRINT_SLOTS)) {
            System.out.print(simulator.now + "\tSlots\t");
            for (Node node : network.nodes) {
                System.out.print(node.reservations.size() + /* ":" + node.reservations.size() + */ "\t");
            }
            System.out.println();
        }
        if (verbose.contains(Verbose.PRINT_X)) {
            System.out.print(simulator.now + "\tX:\t");
            for (Node node : network.nodes) {
                System.out.print(node.getX() + /* ":" + node.reservations.size() + */ "\t");
            }
            System.out.println();
        }

//        simulator.offer(new Event(simulator.now + network.slots.size()
//                * slotTime, EventType.NextFrame, this));
        for (Node node : network.nodes) {
            node.newFrame();
        }

    }

    public Map<Node, Queue<Node>> createAij() {
        Map<Node, Queue<Node>> result = new HashMap<>();

        for (final Node node : network.nodes) {
            if (node.backlog.size() > 0) {
                result.put(node, new PriorityQueue<>(node.neighbors.size(),
                        new Comparator<Node>() {
                    @Override
                    public int compare(Node o1, Node o2) {
                        Double d1 = o1.getX();
                        Double d2 = o2.getX();
                        return -d1.compareTo(d2);
                    }
                }));

                for (Node neighbor : node.neighbors) {
                    if (neighbor.reservations.size() > 0) {
                        if (getSlot(node, neighbor) != null) {
                            result.get(node).offer(neighbor);
                        }
                    }
                }
            }
        }

        return result;
    }

    private class Element implements Comparable<Element> {

        Node node;
        Long u;

        public Element(Node node, Long u) {
            this.node = node;
            this.u = u;
        }

        @Override
        public int compareTo(Element o) {
            return -this.u.compareTo(o.u);
        }
    }

    public void loadBalance(Node[] nodes) {

        Map<Node, Double> u_t_1 = new HashMap<>();

        for (Node node : nodes) {
            double sum_p_j = node.neighbors.stream().mapToDouble(neighbor -> {
                return neighbor.reservations.size();
            }).sum() + node.reservations.size();
            double sum_q_j_1 = node.neighbors.stream().mapToDouble(neighbor -> {
                return neighbor.backlog.size();
            }).sum() + node.backlog.size();
            u_t_1.put(node, node.backlog.size() * sum_p_j / sum_q_j_1 - node.reservations.size());
        }

        Node[] nodes_sorted = nodes.clone();
        Arrays.sort(nodes_sorted, (n1, n2) -> -(u_t_1.get(n1).compareTo(u_t_1.get(n2))));

        for (Node node : nodes_sorted) {
            Node[] neighbors = node.neighbors.toArray(new Node[0]);
            Arrays.sort(neighbors, (n1, n2) -> u_t_1.get(n1).compareTo(u_t_1.get(n2)));

            for (Node neighbor : neighbors) {
                double u_i = u_t_1.get(node);

                if (u_i > 0) {
                    double u_j = u_t_1.get(neighbor);
                    if (u_i > u_j) {
                        int r = (int) Math.round(Math.min(Math.min(u_i, (u_i - u_j) / 2), neighbor.reservations.size() - 1));
                        //System.out.println("Hello!, r= " + r + " u_i= " + u_i + " u_j= " +u_j + " res= " +neighbor.reservations.size() );
                        for (int i = 0; i < r; i++) {
                            Reservation res = getSlot(node, neighbor);
                            if (res != null) {

                                if (verbose.contains(Verbose.PRINT_SLOT_EXCHANGE)) {
                                    System.out.println(neighbor.id + ":" + neighbor.reservations.size() + ":" + neighbor.backlog.size()
                                            + "-->" + node.id + ":" + node.reservations.size() + ":" + node.backlog.size());
                                }
                                res.sender.reservations.remove(res);
                                u_t_1.put(node, u_i - 1);
                                u_i--;
                                u_t_1.put(neighbor, u_j + 1);
                                u_j++;
                                res.slot.reservations.remove(res);
                                res.slot.addNode(node);
                            }
                        }
                    }
                }
            }
        }
    }

    public static class AMax {

        static double[][] a = new double[100][100];
        static int count = 0;

        static void addMatrix(Map<Node, Queue<Node>> a_instance) {
            count++;
            for (Node node : a_instance.keySet()) {
                for (Node neighbor : a_instance.get(node)) {
                    a[node.id][neighbor.id] += 1D / a_instance.get(node).size() * Math.max(1, node.backlog.size()) / Math.max(1, neighbor.backlog.size());
                }
            }
        }

        public static void getMaxAij() {
            if (LocalVoting.verbose.contains(LocalVoting.Verbose.PRINT_DMAX)) {
                for (int i = 0; i < a.length; i++) {
                    for (int j = 0; j < a[i].length; j++) {
                        System.out.print(a[i][j] / count + "\t");
                    }
                    System.out.println();
                }
            }
        }
    }
}
