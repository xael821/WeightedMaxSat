package iset;

/**
 * Created by aellison on 11/21/2016.
 */

import java.util.*;

import iset.RestoreList.*;
import main.S;

public class IndependentSet {

    // http://www8.cs.umu.se/kurser/5DA001/HT08/lab2.pdf


    double best = 0;
    //could turn this into a hashset of collection<node> in case there are ties.
    Collection<Node> bestSolution;
    double[] firstNSum;

    public IndependentSet() {
        bestSolution = new LinkedList<Node>();
    }

    public static void main(String[] args) {
        IndependentSet I = new IndependentSet();
        Graph g = new Graph();
        int n = 40;
        int mMax = 2 * n * (int) Math.sqrt(n);
        Random r = new Random(0);
        for (int i = 0; i < mMax; i++) {
            g.add(r.nextInt(n) + "", r.nextInt(n) + "");
        }
        I.solve(g);
        System.out.println(I.bestSolution.size());
        System.out.println("best: " + I.best);
        I.printSolution(I.bestSolution);

        System.out.println("valid independent set: " + I.checkIndependence(I.bestSolution, g));
    }

    public void solve(Graph g) {
        //store weights in descending order
        int n = g.nodes.size();
        double[] weights = new double[n];
        LinkedList<Node> nodes = g.nodeList();
        for (int i = 0; i < n; i++) {
            weights[i] = nodes.get(i).weight;
        }
        Arrays.sort(weights);
        for (int i = 0; i < n / 2; i++) {
            double temp = weights[i];
            weights[i] = weights[n - i - 1];
            weights[n - i - 1] = temp;
        }
        firstNSum = new double[n];
        firstNSum[0] = weights[0];
        for (int i = 1; i < n; i++) {
            firstNSum[i] = weights[i] + firstNSum[i - 1];
        }
        long time = System.currentTimeMillis();
        best = estimateBound(g, g.nodeList().size());

        g.generateEdgeLists();
        g.sortEdgeLists();
        Node[] nodeArr = g.nodeArray();
        Arrays.sort(nodeArr);
        RestoreList<Node> complement = new RestoreList<>(nodeArr);

        solve(new LinkedList<>(), complement, g);
        System.out.println("time(ms): " + (System.currentTimeMillis() - time));
    }

    //Proof of concept of the recursion, to call: dest = [], src = [x,y...z], returnIndex=-1.
    //optimizations could be to use a skiplist or tree (that supports predecessor). Or a snazzy list, actually, just a list that lets you
    //access nodes directly so you can store that and add to that directly
    private void recursion(List<Double> dest, List<Double> src, int returnIndex) {
        int n = src.size();

        for (Double d : dest) {
            System.out.println(d + " ");
        }
        System.out.println();

        int startIndex = returnIndex < 0 ? 0 : returnIndex;

        for (int i = startIndex; i < n; i++) {
            Double value = src.get(i);
            src.remove(i);
            dest.add(value);

            recursion(dest, src, i);
        }

        if (returnIndex >= 0) {
            Double moveBack = dest.get(dest.size() - 1);
            dest.remove(dest.size() - 1);
            src.add(returnIndex, moveBack);
        }
    }

    private double estimateBound(Graph g, int iterations) {
        Random r = new Random();
        double max = -1;
        for (int i = 0; i < iterations; i++) {
            LinkedList<Node> nodes = g.nodeList();
            LinkedList<Node> iset = new LinkedList<>();
            while (nodes.size() > 0) {
                int index = r.nextInt(nodes.size());
                Node n = nodes.get(index);
                nodes.remove(index);
                for (Node neighbor : g.edgeSets.get(n)) {
                    nodes.remove(neighbor);
                }
            }
            if (!checkIndependence(iset, g)) {
                System.out.println("!");
            }
            double score = solutionWeight(iset);
            if (score > max) {
                max = score;
            }
        }
        return max;
    }

    private void solve(LinkedList<Node> solution, RestoreList<Node> complement, Graph g) {

        boolean condition = best < upperBound(solution, complement, g);

        if (condition) {

            //iterator that allows modification during traversal
            Iterator<Node> complementNodes = complement.iterator();
            while (complementNodes.hasNext()) {
                Node next = complementNodes.next();

                solution.addLast(next);
                makeRestore(next, complement, g);

                //if the actual current weight of this solution is a best, save it
                //we need to check here because the solution has been modified but the complement could be size 0,
                //meaning it may not recurse. Thus, checking at the start of the method won't work.
                double solutionWeight = solutionWeight(solution);
                if (solutionWeight > best) {
                    best = solutionWeight(solution);
                    bestSolution.clear();
                    bestSolution.addAll(solution);
                }

                if (complement.size() > 0) {
                    solve(solution, complement, g);
                }
                complement.rollback();
                solution.removeLast();

            }
        }

    }

    /*
        fills the provided map with key value pairs of graph Nodes to OpenList restore pairs, a pair exists
        for the node remove, as well as everything connected to it in graph

     */
    private void makeRestore(Node remove, RestoreList<Node> complement, Graph g) {
        List<Node> neighbors = g.edgeLists.get(remove);
        if (neighbors == null) {
            System.out.println("failed for " + remove);
        }
        //   ArrayList<Node> intersection = intersection(neighbors, complement);
        // ArrayList<Node> intersection2= intersection2(neighbors, complement);
        complement.openTransaction();
        for (Node n : neighbors) {
            complement.remove(n);
        }
        complement.remove(remove);

        complement.closeTransaction();
    }

    private static void printSolution(Collection<Node> solution) {
        System.out.println("solution: ");
        for (Node n : solution) {
            System.out.print(n.identifier + " ");
        }
        System.out.println();
    }

    private double solutionWeight(LinkedList<Node> solution) {
        double out = 0;
        for (Node n : solution) {
            out += n.weight;
        }
        return out;
    }

    private double upperBound(LinkedList<Node> solution, RestoreList<Node> complement, Graph g) {
        int n = 0, m = 0;
        double baseLine = solutionWeight(solution);

        HashSet<Node> temp = new HashSet<Node>();
        for (Node c : complement) {
            boolean valid = true;
            for (Node s : solution) {
                if (g.edgeExists(c, s)) {
                    valid = false;
                    break;
                }
            }
            if (valid) {
                temp.add(c);
            }
        }

        n = temp.size();
        for (Node c : temp) {
            for (Node d : temp) {
                if (c != d && g.edgeExists(c, d)) {
                    m++;
                }
            }
        }
        m /= 2;
        double out = baseLine + upperBound(n, m);
        return out;
    }

    private double upperBound(int n, int m) {
        // (1/2)*(1+Sqrt[1-8m-4n+ 4n^2]) is the number of nodes
        //round down
        int maxN = (int) (0.5 * (1 + Math.sqrt(1 - 8 * m - 4 * n + 4 * n * n)));
        return firstNSum[maxN];
    }

    public static boolean checkIndependence(Collection<Node> solution, Graph g) {

        for (Node n1 : solution) {
            for (Node n2 : solution) {
                if (n1 != n2 && g.edgeExists(n1, n2)) {
                    return false;
                }
            }
        }
        return true;
    }

}

/*
if we make the list and the node separate classes then we can maintain a tail pointer in constant time.
this can actually become a safe implementation if vars are protected and everything is method based.

 */
