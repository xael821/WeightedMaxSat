package main;

/**
 * Created by aellison on 11/21/2016.
 */

import java.util.*;

public class WorkingSet implements Iterable<Clause> {

    //clauses and weights are indexed such that weights[i] is the weight assigned to clauses[i]
    final private Clause[] clauses;
    final private double[] weights;
    //this array contains all variables in the clauses stored above
    final private Variable[] vars;
    final private int n;
    //adjacency matrix of graph where clauses are nodes and edges exist between mutually unsatisfiable clauses.
    double[][] conflictMatrix;


    private WorkingSet(Clause[] clauses, double[] weights, Variable[] vars) {
        this.clauses = clauses;
        this.weights = weights;
        this.vars = vars;
        n = clauses.length;
    }

    //this method will eventually make calls to clean out clauses that can't be satisfied etc
    public static WorkingSet createWorkingSet(List<Clause> clauses, List<Double> weights, Collection<Variable> vars) {
        Clause[] clauseArr = clauses.toArray(new Clause[0]);
        Double[] DoubleArr = weights.toArray(new Double[0]);
        double[] doubleArr = new double[DoubleArr.length];
        for (int i = 0; i < doubleArr.length; i++) {
            doubleArr[i] = DoubleArr[i];
        }
        Variable[] varArr = vars.toArray(new Variable[0]);
        return new WorkingSet(clauseArr, doubleArr, varArr);
    }

    public Iterator<Clause> iterator() {
        return new ClauseIterator(clauses);
    }

    public double score(){
        double out = 0;
        for(int i =0 ;i < n; i++){
            if(clauses[i].evaluate()){
                out+=weights[i];
            }
        }
        return out;
    }

    public void writeAssignmentToArray(boolean[] arr){
        if(arr.length!=n){
            throw new RuntimeException("mismatch between array size and number of variables");
        }
        for(int i = 0; i<n;i++){
            arr[i]=vars[i].evaluate();
        }
    }

    public int n(){
        return n;
    }

    public int varNum(){
        return vars.length;
    }

    public void setVariable(int i, boolean value){
        vars[i].setValue(value);
    }

    public boolean getVariable(int i){
        return vars[i].evaluate();
    }

    public void print(){
        for(int i = 0; i< vars.length;i++){
            System.out.print(vars[i].evaluate()+" ");
        }
        System.out.println();
    }

}

class ClauseIterator implements Iterator<Clause> {

    int index = 0;
    Clause[] clauses;

    public ClauseIterator(Clause[] clauses) {
        this.clauses = clauses;
    }

    public boolean hasNext() {
        return index < clauses.length;
    }

    public Clause next() {
        Clause out = clauses[index];
        index++;
        return out;
    }
}