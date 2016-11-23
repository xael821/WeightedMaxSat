package main;

/**
 * Created by aellison on 11/18/2016.
 */

import Shunting.ShuntingYard;

import java.text.ParseException;
import java.util.HashMap;
import java.util.*;

public class Parser {

    private HashMap<String, Variable> clauseVariables = new HashMap<>();

    //as an example of functionality
    public static void main(String[] args) {
        precedenceTest();
    }

    private static void precedenceTest(){
        //we expect not to be the most tightly binding so we want (!a) and b not !(a and b)
        Parser p = new Parser();
        Clause c =p.parse("not a and b");
        System.out.println(c);
    }

    private static void variableTest() { /*
            clauses: {a or b and c} {b and !c and !d}
            evaluate for (a,b,c,d) = (0,0,0,0)
                expected: false, false
            evaluate for (a,b,c,d) = (1,0,0,0)
                expected: true, false
            evaluate for (a,b,c,d) = (1,1,0,0)
                expected: true, true
            evaluate for (a,b,c,d) = (0,0,0,0)
                expected: false, false
         */
        Parser p = new Parser();
        LinkedList<String> inputs = new LinkedList<>();
        inputs.add("a or b and c");
        inputs.add("b and not c and not d");
        LinkedList<Double> weights=  new LinkedList<Double>();

        WorkingSet output = p.parseMultiple(inputs);
        Collection<Variable> vars = p.getClauseVariables();
        Variable[] varray = vars.toArray(new Variable[0]);
        for (Clause c : output) {
            System.out.println(c.evaluate());
        }
        varray[0].setValue(true);
        for (Clause c : output) {
            System.out.println(c.evaluate());
        }
        varray[1].setValue(true);
        for (Clause c : output) {
            System.out.println(c.evaluate());
        }
        varray[0].setValue(false);
        varray[1].setValue(false);
        for (Clause c : output) {
            System.out.println(c.evaluate());
        }
    }

    ShuntingYard s;

    public Parser(LinkedList<String> ops) {
        s = new ShuntingYard(ops);
    }

    public Parser() {
        s = new ShuntingYard(defaultOps());
    }

    private LinkedList<String> defaultOps() {
        LinkedList<String> ops = new LinkedList<>();
        ops.add("not");
        ops.add("and");
        ops.add("or");
        ops.add("xor");
        ops.add("implies");
        ops.add("iff");
        return ops;
    }

    /*
        this is method parses a list of clauses and creates them all with the same variable objects, so a1 in clause1
        and a1 in clause51 are the same. This will allow for clauses' evaluations to be modified together just by
        modifying the variable.

        This does not return the set of clauseVariables, but each clause supports finding its set of comprising clauseVariables.
     */
    public WorkingSet parseMultiple(List<String> inputs, List<Double> weights) {
        LinkedList<Clause> out = new LinkedList<>();
        HashMap<String, Variable> vars = new HashMap<>();
        for (String input : inputs) {
            Clause clause = parse(input, vars);
            out.add(clause);
        }
        this.clauseVariables = vars;
        return WorkingSet.createWorkingSet(out, weights, vars.values());
    }

    public WorkingSet parseMultiple(List<String> inputs) {
        return parseMultiple(inputs,null);
    }

    public Clause parse(String input) {
        return parse(input, new HashMap<String, Variable>());
    }

    //does not assume anything about spacing other than it exists between operators and operands
    public Clause parse(String input, HashMap<String, Variable> vars) {
        input = clean(input);
        String postfix = s.postfix(input);
        try {
            return processPostfix(postfix, s, vars);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static Clause processPostfix(String postfix, ShuntingYard s) throws ParseException {
        HashMap<String, Variable> vars = new HashMap<>();
        return processPostfix(postfix, s, vars);
    }

    //assumes single spaces operators and clauseVariables
    private static Clause processPostfix(String postfix, ShuntingYard s, HashMap<String, Variable> vars) throws ParseException {
        postfix = postfix.trim();
        String[] tokens = postfix.split(" ");
        LinkedList<Clause> stack = new LinkedList<>();

        for (String token : tokens) {
            if (s.isOperator(token)) {
                token = token.trim();
                //not is special because it's unary
                if (token.equals("not")) {
                    Clause pop = stack.pop();
                    NotClause n = new NotClause(pop);
                    stack.push(n);
                } else {
                    Clause a = stack.pop();
                    Clause b = stack.pop();
                    stack.push(new BinaryClause(b, a, Operator.createFromString(token)));
                }
            } else {
                Variable v;
                if (vars.containsKey(token)) {
                    v = vars.get(token);
                } else {
                    v = new Variable(token);
                    vars.put(token, v);
                }
                stack.push(new Leaf(v));
            }
        }

        return stack.pop();
    }

    private static String clean(String input) {
        String out = input.replace("(", " ( ");
        out = out.replace(")", " ) ");
        while (out.contains("  ")) {
            out = out.replace("  ", " ");
        }
        out = out.trim();
        return out;
    }

    public Collection<Variable> getClauseVariables() {
        return this.clauseVariables.values();
    }
}
