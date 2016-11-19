package Shunting; /**
 * Created by aellison on 11/18/2016.
 */

import java.util.*;

public class ShuntingYard {

    public static void main(String[] args) {

        LinkedList<String> ops = new LinkedList<>();
        ops.add("*");
        ops.add("/");
        ops.add("+");
        ops.add("-");
        ShuntingYard s = new ShuntingYard(ops);
        System.out.println(s.postfix("( 1 + 2 ) * 3 + 4"));
    }

    HashMap<String, Integer> precedences = new HashMap<>();

    public ShuntingYard(List<String> operators) {
        if (operators != null) {
            Iterator<String> iter = operators.iterator();
            int i = 0;
            while (iter.hasNext()) {
                String op = iter.next();
                precedences.put(op, i);
                i--;
            }
        }
    }

    private int compare(String a, String b) {
        if (!precedences.containsKey(a) || !precedences.containsKey(b)) {
            return -1;
        }
        return Integer.compare(precedences.get(a), precedences.get(b));
    }

    public String postfix(String in) {
        String input = in + "";
        StringBuilder output = new StringBuilder();
        Deque<String> stack = new LinkedList<>();

        for (String token : input.split(" ")) {
            // operator
            if (precedences.containsKey(token)) {
                while (!stack.isEmpty() && compare(stack.peek(), token) >= 0)
                    output.append(stack.pop()).append(' ');
                stack.push(token);

                // left paren
            } else if (token.equals("(")) {
                stack.push(token);

                // right paren
            } else if (token.equals(")")) {
                while (!stack.peek().equals("("))
                    output.append(stack.pop()).append(' ');
                stack.pop();

                // operand
            } else {
                output.append(token).append(' ');
            }
        }

        while (!stack.isEmpty())
            output.append(stack.pop()).append(' ');

        return output.toString();
    }

    public boolean isOperator(String op) {
        return precedences.containsKey(op);
    }
}