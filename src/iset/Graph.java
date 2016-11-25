package iset;

import java.util.*;

/**
 * Created by aellison on 11/22/2016.
 */
class Graph {

    HashMap<String, Node> nodes;
    HashMap<Node, HashSet<Node>> edgeSets;
    HashMap<Node, List<Node>> edgeLists;

    public Graph() {
        nodes = new HashMap<>();
        edgeSets = new HashMap<>();
        edgeLists = new HashMap<>();
    }

    public void add(String u, String v) {
        if(u.equals(v)){
            return;
        }
        Node U, V;
        if (!nodes.containsKey(u)) {
            Node temp = new Node(u);
            nodes.put(u, temp);
            edgeSets.put(temp, new HashSet<>());
        }
        U = nodes.get(u);

        if (!nodes.containsKey(v)) {
            Node temp = new Node(v);
            nodes.put(v, temp);
            edgeSets.put(temp, new HashSet<>());
        }
        V = nodes.get(v);
        //we now have U,V defined in both hashmaps, so no null exceptions should occur.
        edgeSets.get(V).add(U);
        edgeSets.get(U).add(V);
    }

    public void generateEdgeLists() {
        for (Node n : edgeSets.keySet()) {
            LinkedList<Node> list = new LinkedList<>();
            list.addAll(edgeSets.get(n));
            edgeLists.put(n, list);
        }
    }

    public void sortEdgeLists(){
        for(Node n : edgeLists.keySet()){
            Node[] arr = edgeLists.get(n).toArray(new Node[0]);
            Arrays.sort(arr);
            edgeLists.put(n, Arrays.asList(arr));
        }
    }

    public LinkedList<Node> nodeList() {
        Collection c = nodes.values();
        LinkedList<Node> out = new LinkedList<>();
        out.addAll(c);
        return out;
    }

    public HashSet<Node> nodeSet() {
        Collection c = nodes.values();
        HashSet<Node> out = new HashSet<>();
        out.addAll(c);
        return out;
    }

    public Node[] nodeArray(){
        return nodes.values().toArray(new Node[0]);
    }

    public boolean edgeExists(Node node1, Node node2) {
        return edgeSets.get(node1).contains(node2);
    }

    public int m() {
        int out = 0;
        for (HashSet<Node> edgeList : edgeSets.values()) {
            out += edgeList.size();
        }
        out /= 2;
        return out;
    }

}

class Node implements Comparable<Node> {

    String identifier;
    double weight;

    public Node(String identifier, double weight) {
        this.identifier = identifier;
        this.weight = weight;
    }

    public Node(String identifier) {
        this(identifier, 1);
    }

    public int compareTo(Node other){
        return identifier.compareTo(other.identifier);
    }

    public String toString(){
        return identifier;
    }
}
