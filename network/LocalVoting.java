/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import application.Statistics;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
        PRINT_SLOT_EXCHANGE
    }
    public static Verbose verbose;

    public LocalVoting(Network network, Simulator simulator, double slotTime) {
        super(network, simulator, slotTime);
    }

    @Override
    void nextFrame() {
        currentFrame++;
//        currentSlot = 0;
        for (Node node : network.nodes) {
            if (node.backlog.isEmpty()) {
                while (node.reservations.size() > 0) {
                    node.removeSlot();
                    if (verbose == verbose.PRINT_W && simulator.now > Statistics.starttime) {
                        System.out.println(node.id + "\t-1");
                    }
                }
            }
        }

        for (Node node : network.nodes) {

            if (!node.backlog.isEmpty()) {
                if (!network.findSlot(node)) {
                    //    loadBalance(node);
//                    System.out.println("No slots available!!!");
                } else if (verbose == verbose.PRINT_W && simulator.now > Statistics.starttime) {
                    System.out.println(node.id + "\t1");
                }
            } 
        }

        loadBalance();

        if (verbose == Verbose.PRINT_QUEUE_LENGTHS) {
            System.out.print(simulator.now + "\t");
            for (Node node : network.nodes) {
                System.out.print(node.backlog.size() + "\t");
            }
            System.out.println();
        }

//        simulator.offer(new Event(simulator.now + network.slots.size()
//                * slotTime, EventType.NextFrame, this));
    }

    public Map<Node, Queue<Node>> createAij() {
        Map<Node, Queue<Node>> result = new HashMap<>();

        for (Node node : network.nodes) {
            if (node.backlog.size() > 0) {
                result.put(node, new PriorityQueue<>(node.neighbors.size(),
                        new Comparator<Node>() {
                    @Override
                    public int compare(Node o1, Node o2) {
                        return -o1.getX().compareTo(o2.getX());
                    }
                }));
                for (Node neighbor : node.neighbors) {
                    if (neighbor.backlog.size() > 0) {
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

    public void loadBalance() {

        Map<Node, Queue<Node>> aij = createAij();

        //Map<Node, Long> u = new HashMap<>();

        Queue<Element> u = new PriorityQueue<>();

        for (Node node : network.nodes) {
            if (aij.containsKey(node)) {
                double u_temp = 0D;
                for (Node neighbor : aij.get(node)) {
                    if (neighbor.backlog.size() > 0) {
                        u_temp += (neighbor.getX() - node.getX())/aij.get(node).size();
                    }
                }
                u.offer(new Element(node, (long) Math.ceil(u_temp)));
            }
        }
        if (verbose == Verbose.PRINT_SLOT_EXCHANGE) {
            long s = 0L;
            for (Element e : u) {
                System.out.print(e.node.id + ":" + e.u + "\t");
                s += e.u;
            }
            System.out.println("total:\t" + s);
        }

        while (u.size() > 0 && u.peek().u > 0) {
            Element e = u.poll();

            boolean found = false;
            Set<Node> removed = new HashSet<>();
            while (!aij.get(e.node).isEmpty()) {
                Node other = aij.get(e.node).poll();
                removed.add(other);
                Reservation res = getSlot(e.node, other);
                if (res != null && (other.getX() > e.node.getX())) {
                    if (verbose == Verbose.PRINT_SLOT_EXCHANGE) {
                        System.out.println(other.id + ":" + other.reservations.size() + ":" + other.backlog.size()
                                + "-->" + e.node.id + ":" + e.node.reservations.size() + ":" + e.node.backlog.size());
                    }
                    res.sender.reservations.remove(res);
                    res.slot.reservations.remove(res);
                    res.slot.addNode(e.node);
                    e.u--;
                    found = true;
                    break;
                }
            }
            aij.get(e.node).addAll(removed);
            if (found) {
                u.offer(e);
            }
        }
        if (verbose == Verbose.PRINT_SLOT_EXCHANGE) {
            long s = 0L;
            for (Element e : u) {
                System.out.print(e.node.id + ":" + e.u + "\t");
                s += e.u;
            }
            System.out.println("total2:\t" + s);
        }
    }
}
