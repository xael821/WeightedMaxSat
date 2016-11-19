package main; /**
 * Created by aellison on 11/18/2016.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import java.util.*;

public class S {

    public static void main(String[] args) throws java.lang.Exception {
        // randomizedTest(0,1009);
        Graph g = new Graph();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input = br.readLine();
        while (input != null) {
            input = input.trim();
            handleLine(input, g);
            input = br.readLine();
        }

    }

    public static void handleLine(String line, Graph g) {
        if (line == null) {
            return;
        }
        String[] words = line.split(" ");
        //return if wrong number of args
        if (words.length != 3 && words.length != 4) {
            return;
        }
        //return if empty string is either node
        if (words[words.length - 1].length() < 1 || words[words.length - 2].length() < 1) {
            return;
        }
        if (words[0].equals("add")) {
            //add
            g.addSymmetricEdge(words[1], words[2]);
        } else if (words[0].equals("is") && words[1].equals("linked")) {
            //is linked
            System.out.println(g.isConnected(words[2], words[3]));
        } else if (words[0].equals("remove")) {
            //remove
            g.removeSymmetricEdge(words[1], words[2]);
        }
        //else, invalid command.
    }

    /* this test does not cover all possible bugs, but it makes sure that
     transitivity and symmetry hold for 'is linked' and can give a rough measure
     of time performance.
     */
    public static void randomizedTest(int seed, int n) {
        Random r = new Random(seed);
        Graph g = new Graph();

        for (int i = 0; i < n; i++) {
            //  System.out.println(i);
            int a = r.nextInt(n);
            int b = r.nextInt(n);
            g.addSymmetricEdge(a + "", b + "");
        }

        for (int i = 0; i < n * 5; i++) {

            int a = r.nextInt(n);
            int b = r.nextInt(n);
            g.addSymmetricEdge(a + "", b + "");
            a = r.nextInt(n);
            b = r.nextInt(n);

            g.removeSymmetricEdge(a + "", b + "");
            a = r.nextInt(n);
            b = r.nextInt(n);
            int c = r.nextInt(n);

            boolean ab = g.isConnected("" + a, "" + b);
            boolean ba = g.isConnected("" + b, "" + a);
            boolean bc = g.isConnected("" + c, "" + b);
            boolean cb = g.isConnected("" + b, "" + c);
            boolean ac = g.isConnected("" + a, "" + c);
            boolean ca = g.isConnected("" + c, "" + a);

            //symmetry:
            if (ab != ba || bc != cb || ac != ca) {
                System.out.println("symmetry broken");
            }
            //transitivity
            if ((ab && bc && !ac) || (ab && ac && !bc) || (ac && bc && !ab)) {
                System.out.println("transitivity broken");
            }
        }
        System.out.println("test exited");
    }

}

/*
 This is an implementation of a graph which is implemented as a digraph,
 however all edges are bidirectional (or as I used in method names, 'symmetrical.'

 The values of nodes are represented as strings, which is very flexible and
 allows for larger numbers than any primitive type (it wasn't clear how large
 is from "we test really big positive integers.").
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 A basic overview of the implementation:

 Nodes have a list of references to the nodes it has directed edges to.

 The graph stores a hashmap mapping values to the graph nodes containing
 those values.

 There are two hashmaps that work in tandem to provide constant time access
 for the 'is linked' functionality. The first maps values (nodes basically) to
 an integer identifying the graph component that node is in. The second one
 maps those component identifiers to HashSets of the nodes in that component.
 We need constant time access to any node in a component for removing quickly.

 Component identities are represented by integers. Because the number of components
 possible on any normal machine/JVM is much less than 2^32, picking random ints
 and checking if they're already used works perfectly fine. We could easily switch
 to longs if need be.

 More detailed explanations of how each method works can be found in the class.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 Analysis of space (n = number of nodes, L = number of edges/links):

 There is an object allocated for each node, and each has a reference for each
 node it is attached to. Each link requires two pointers because they are
 directed. The space taken there is O(n+L).

 The hashmap of values to nodes is O(n), there is one entry for each node.

 The hashmap valueToComponent is O(n), there is one entry for each node.

 The hashmap componentNodes has one key per component, and the values are
 hashsets which collectively contain each node in the graph exactly once (every
 node is in exactly one component, even if it is on its own).
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 Time complexity provided at each method.
 */
class Graph {

    //this is the map of values to node objects containing those values
    private HashMap<String, Node> nodes = new HashMap<String, Node>();

    private HashMap<String, Integer> valueToComponent = new HashMap<>();
    private HashMap<Integer, HashSet<Node>> componentNodes = new HashMap<>();
    private Random r = new Random();

    private int nextId() {
        int temp;
        do {
            temp = r.nextInt();
        } while (componentNodes.containsKey(temp));
        return temp;
    }

    private void add(String value) {
        nodes.put(value, new Node(value));
    }

    /*
     adds the edge between nodes with values: value1 and value2.

     In the worst case, we need to merge components which is O(n).
     However, expected runtime is lower.
     */
    public void addSymmetricEdge(String value1, String value2) {
        //trivial case, nodes are always self linked:
        if (value1.equals(value2)) {
            return;
        }

        //add the nodes to set of nodes if they don't already exist, flag if so
        boolean new1 = false;
        boolean new2 = false;
        if (!nodes.containsKey(value1)) {
            add(value1);
            new1 = true;
        }
        if (!nodes.containsKey(value2)) {
            add(value2);
            new2 = true;
        }
        Node node1 = nodes.get(value1);
        Node node2 = nodes.get(value2);

        /* If both are new nodes, then they are their own component.

         If only one is new, then the new one is added to the existing
         component of the other.

         If neither is new, this could either be a merge of two components
         or just adding another edge inside an existing one. main.Iff it's the
         former then there are new pairs of nodes that satisfy 'is linked'
         */
        //update component information:
        if (new1 && new2) {
            int id = nextId();
            valueToComponent.put(value1, id);
            valueToComponent.put(value2, id);
            HashSet<Node> nodeSet = new HashSet<Node>();
            nodeSet.add(node1);
            nodeSet.add(node2);
            componentNodes.put(id, nodeSet);
        } else if (new1 != new2) {
            if (new1) {
                int id = valueToComponent.get(value2);
                valueToComponent.put(value1, id);
                componentNodes.get(id).add(node1);
            } else {
                int id = valueToComponent.get(value1);
                valueToComponent.put(value2, id);
                componentNodes.get(id).add(node2);
            }
        } else {//neither is new
            int component1 = valueToComponent.get(value1);
            int component2 = valueToComponent.get(value2);

            //if not connected:
            if (component1 != component2) {
                //move elements of component 1 into component 2
                HashSet<Node> component1Nodes = componentNodes.get(component1);
                for (Node n : component1Nodes) {
                    valueToComponent.put(n.getValue(), component2);
                }
                componentNodes.get(component2).addAll(component1Nodes);
            }
        }
        //add the directional edges regardless of component status
        node1.addSuccessor(node2);
        node2.addSuccessor(node1);
    }

    /*
        removes the edge between nodes with values: value1 and value2, if they
        exist in the graph.

        This calls main.Node's connectedTo every time, which is O(n+L). Removing elements
        from component's node sets is O(n), as is adding them all to another.
        Runtime is O(n+L).
     */
    public void removeSymmetricEdge(String value1, String value2) {
        //if either node isn't in the graph, there is no edge to remove
        if (!nodes.containsKey(value1) || !nodes.containsKey(value2)) {
            return;
        }

        Node node1 = nodes.get(value1);
        Node node2 = nodes.get(value2);

        //delete the directed edges
        node1.removeSuccessor(node2);
        node2.removeSuccessor(node1);

        //if they are now in separate components...
        if (!node1.connectedTo(value2)) {
            int currentComponentId = valueToComponent.get(value1);
            HashSet<Node> component2 = node2.component();

            HashSet<Node> currentList = componentNodes.get(currentComponentId);
            currentList.removeAll(component2);

            int newComponentId = nextId();
            for (Node n : component2) {
                valueToComponent.put(n.getValue(), newComponentId);
            }

            HashSet<Node> newComponentSet = new HashSet<Node>();
            newComponentSet.addAll(component2);
            componentNodes.put(newComponentId, newComponentSet);
        }

        //remove the nodes if their degree is zero - they'll be added back with the add command if needed.
        //This prevents unused/useless nodes from building up in some weird usage scenario.
        if (!node1.hasSuccessor()) {
            nodes.remove(node1.getValue());
            int comp = valueToComponent.get(node1.getValue());
            componentNodes.get(comp).remove(node1);
        }
        if (!node2.hasSuccessor()) {
            nodes.remove(node2.getValue());
            int comp = valueToComponent.get(node2.getValue());
            componentNodes.get(comp).remove(node2);
        }
    }

    // constant time: O(1)
    public boolean isConnected(String value1, String value2) {
        if (!nodes.containsKey(value1) || !nodes.containsKey(value2)) {
            return false;
        }
        return valueToComponent.get(value1).equals(valueToComponent.get(value2));
    }

}

/*
 this is a node class for a node in a directed graph. Although the challenge
 said the graph node ids would all be positive integers, it was said they would
 be quite large... I wasn't sure if that included numbers bigger than ~2^64 so
 rather than use a long I used a string. This has the benefit of being more
 generalizable, although hashing is slower.
 */
class Node {

    final private String value;
    final private LinkedList<Node> successors = new LinkedList<Node>();

    public Node(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    //prepending to linkedlist is O(1) time
    protected void addSuccessor(Node n) {
        successors.addFirst(n);
    }

    //if N is the length of the list, time is O(N). In a graph with n edges, and
    //L links this is O(min(n,L)) which is O(n)
    public void removeSuccessor(Node removee) {
        successors.remove(removee);
    }

    public boolean hasSuccessor() {
        return successors.size() > 0;
    }

    /*this routine is useful when the graph needs to remove a pair because
     when an edge is deleted, we can check if the pair is still connected.
     If not, then we delete the cartesian product of the nodes in each
     component from the set of connected pairs in main.Graph
     */
    public boolean connectedTo(String target) {
        return connectedTo(target, new HashSet<String>());
    }

    private boolean connectedTo(String target, HashSet<String> markedNodes) {
        //if the target = value of this node, return true.
        if (target.equals(value)) {
            return true;
        }
        //mark self as visited
        markedNodes.add(value);
        //depth first search
        for (Node n : successors) {
            //if unmarked, continue recursion
            if (!markedNodes.contains(n.getValue())) {
                boolean result = n.connectedTo(target, markedNodes);
                if (result) {
                    return true;
                }
            }
        }
        return false;
    }

    public HashSet<Node> component() {
        HashSet<Node> nodes = new HashSet<Node>();
        findComponent(nodes);
        return nodes;
    }

    //this is just depth first search and adding each node to a hashset (which
    //has constant time insertion) so this is O(n+L) for n nodes and L links
    private void findComponent(HashSet<Node> markedNodes) {
        //mark self as visited
        markedNodes.add(this);
        //depth first search
        for (Node n : successors) {
            //if unmarked, continue recursion
            if (!markedNodes.contains(n)) {
                n.findComponent(markedNodes);
            }
        }
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        Node other = (Node) o;
        if (other.value == null || value == null) {
            return false;
        }
        return other.value.equals(value);
    }
}
