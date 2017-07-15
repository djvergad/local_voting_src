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
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
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
    public static Verbose verbose;
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
                while (node.reservations.size() > 0) {
                    node.removeSlot();
                    if (verbose == verbose.PRINT_W && simulator.now > Statistics.starttime) {
                        System.out.println(node.id + "\t-1");
                    }
                }
            }
        }

        List<Node> nodes_shuffled = new ArrayList(network.nodes);
        Collections.shuffle(nodes_shuffled);
        for (Node node : nodes_shuffled) {

            if (!node.backlog.isEmpty()) {
                if (!network.findSlot(node)) {
                    //    loadBalance(node);
//                    System.out.println("No slots available!!!");
                } else if (verbose == verbose.PRINT_W && simulator.now > Statistics.starttime) {
                    System.out.println(node.id + "\t1");
                }
            }
        }

        loadBalance(nodes_shuffled);

        if (verbose == Verbose.PRINT_QUEUE_LENGTHS) {
            System.out.print(simulator.now + "\t");
            for (Node node : network.nodes) {
                System.out.print(node.backlog.size() + /* ":" + node.reservations.size() + */ "\t");
            }
            System.out.println();
        } else if (verbose == Verbose.PRINT_SLOTS) {
            System.out.print(simulator.now + "\t");
            for (Node node : network.nodes) {
                System.out.print(node.reservations.size() + /* ":" + node.reservations.size() + */ "\t");
            }
            System.out.println();
        } else if (verbose == Verbose.PRINT_X) {
            System.out.print(simulator.now + "\t");
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

    public void loadBalance(List<Node> nodes) {

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

        int[] slots_gained_or_lost = new int[network.nodes.size()];

        for (Node node : nodes) {
            Node[] neighbors = node.neighbors.toArray(new Node[0]);
            Arrays.sort(neighbors, (n1, n2) -> u_t_1.get(n1).compareTo(u_t_1.get(n2)));

            double u_i = u_t_1.get(node);

            for (Node neighbor : neighbors) {
                if (u_i - slots_gained_or_lost[node.id] > 0) {
                    double u_j = u_t_1.get(neighbor);
                    if (u_i < u_j) {
                        long r = Math.round(Math.min(Math.min(u_i, (u_i - u_j) / 2), neighbor.reservations.size() - 1));

                        for (int i = 0; i < r; i++) {
                            Reservation res = getSlot(node, neighbor);
                            if (res != null) {
                                if (verbose == Verbose.PRINT_SLOT_EXCHANGE) {
                                    System.out.println(neighbor.id + ":" + neighbor.reservations.size() + ":" + neighbor.backlog.size()
                                            + "-->" + node.id + ":" + node.reservations.size() + ":" + node.backlog.size());
                                }
                                res.sender.reservations.remove(res);
                                slots_gained_or_lost[node.id]++;
                                slots_gained_or_lost[neighbor.id]--;
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
            if (LocalVoting.verbose == LocalVoting.Verbose.PRINT_DMAX) {
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
