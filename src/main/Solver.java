package main;

/**
 * Created by aellison on 11/21/2016.
 */

import java.util.*;

public class Solver {

    double currentBest = -1;
    boolean[] bestAssignment;
    WorkingSet workingSet;
    public Solver(WorkingSet w) {
        workingSet=w;
        bestAssignment = new boolean[w.n()];
    }

    public void solve(){
        int v = workingSet.varNum();
        for(int i = 0;i < v; i++){
            workingSet.setVariable(i,false);
        }
        solve(v-1,false);
        solve(v-1,true);
    }

    //the variable at index depth will be set to value.
    private void solve(int depth, boolean value) {
        //if all solutions derived from this one can't improve our solution, return.
        if(upperBoundOfPartialSolution()<currentBest){
            return;
        }
        boolean originalValue = workingSet.getVariable(depth);
        workingSet.setVariable(depth, value);
        double score = workingSet.score();

        if(score > currentBest){
            currentBest = score;
            workingSet.writeAssignmentToArray(bestAssignment);
        }

        if(depth > 0){
            solve(depth-1,true);
            if(depth>1) {
                solve(depth - 2, true);
            }
        }
        workingSet.setVariable(depth, originalValue);
    }

    private double upperBoundOfPartialSolution(){
        return Double.MAX_VALUE;
    }

    private double score() {
      return 0;
    }

}
