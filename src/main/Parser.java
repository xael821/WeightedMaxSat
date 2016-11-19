package main;

/**
 * Created by aellison on 11/18/2016.
 */

import Shunting.ShuntingYard;

import java.text.ParseException;
import java.util.HashMap;
import java.util.*;

public class Parser {
    //as an example of functionality
    public static void main(String[] args) {
        Clause c = new Parser().parse("(not b)and ( b iff ( a xor c ) ) and ( c or a )");
        System.out.println(c);
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

        This does not return the set of variables, but each clause supports finding its set of comprising variables.
     */
    public List<Clause> parseMultiple(List<String> inputs) {
        LinkedList<Clause> out = new LinkedList<>();
        HashMap<String, Variable> vars = new HashMap<>();
        for (String input : inputs) {
            Clause clause = parse(input, vars);
        }
        return out;
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

    //assumes single spaces operators and variables
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
}
