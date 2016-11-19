package main;

/**
 * Created by aellison on 11/18/2016.
 */
import Shunting.ShuntingYard;

import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;

public class Parser {
    //as an example of functionality
    public static void main(String[] args){
        Clause c= new Parser().parse("(not b)and ( b iff ( a xor c ) ) and ( c or a )");
        System.out.println(c);
    }

    ShuntingYard s;

    public Parser(LinkedList<String> ops){
        s = new ShuntingYard(ops);
    }

    public Parser(){
        s = new ShuntingYard(defaultOps());
    }

    private LinkedList<String> defaultOps(){
        LinkedList<String> ops = new LinkedList<>();
        ops.add("not");
        ops.add("and");
        ops.add("or");
        ops.add("xor");
        ops.add("implies");
        ops.add("iff");
        return ops;
    }

    //does not assume anything about spacing other than it exists between operators and operands
    public  Clause parse(String input){
        input = clean(input);
        String postfix = s.postfix(input);
        try {
            return processPostfix(postfix, s);
        }catch(ParseException e){
            throw new RuntimeException(e);
        }
    }

    //assumes single spaces operators and variables
    private static Clause processPostfix(String postfix, ShuntingYard s) throws ParseException{
        postfix= postfix.trim();
        String[] tokens = postfix.split(" ");
        LinkedList<Clause> stack = new LinkedList<>();
        HashMap<String, Variable> vars = new HashMap<>();

        for(String token: tokens){
            if(s.isOperator(token)){
                token = token.trim();
                //not is special because it's unary
                if(token.equals("not")){
                    Clause pop = stack.pop();
                    NotClause n = new NotClause(pop);
                    stack.push(n);
                }else{
                    Clause a = stack.pop();
                    Clause b = stack.pop();
                    stack.push(new BinaryClause(b,a, Operator.createFromString(token)));
                }
            }else{
                Variable v;
                if(vars.containsKey(token)) {
                    v = vars.get(token);
                }else{
                     v = new Variable(token);
                    vars.put(token,v);
                }
                stack.push(new Leaf(v));
            }
        }

        return stack.pop();
    }

    private static String clean(String input){
        String out = input.replace("(", " ( ");
         out = out.replace(")", " ) ");
        while(out.contains("  ")){
            out = out.replace("  "," ");
        }
        out = out.trim();
        return out;
    }
}
