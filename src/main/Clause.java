package main;

import java.text.ParseException;
import java.util.*;

/**
 * Created by aellison on 11/18/2016.
 */
abstract public class Clause {
    abstract public boolean evaluate();

    abstract public Set<Variable> getVariables();
}

class BinaryClause extends Clause {
    final Clause left, right;
    final BinaryOperator operator;

    public BinaryClause(Clause left, Clause right, BinaryOperator operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public boolean evaluate() {
        return operator.operate(left.evaluate(), right.evaluate());
    }

    public Set<Variable> getVariables() {
        Set<Variable> vars = left.getVariables();
        vars.addAll(right.getVariables());
        return vars;
    }

    public String toString() {
        return "(" + left.toString() + " " + operator.toString() + " " + right.toString() + ")";
    }
}

class NotClause extends Clause {
    final Clause clause;

    public NotClause(Clause clause) {
        this.clause = clause;
    }

    public NotClause(Variable v) {
        this(new Leaf(v));
    }

    public boolean evaluate() {
        return !clause.evaluate();
    }

    public Set<Variable> getVariables() {
        return clause.getVariables();
    }

    public String toString() {
        return "!" + clause.toString();
    }
}

class Leaf extends Clause {
    final Variable var;

    public Leaf(Variable var) {
        this.var = var;
    }

    public boolean evaluate() {
        return var.evaluate();
    }

    public String toString() {
        return var.toString();
    }

    public Set<Variable> getVariables() {
        HashSet<Variable> set = new HashSet<>();
        set.add(var);
        return set;
    }
}


class Variable {
    private String identifier;
    private boolean value;

    public Variable(String identifier, boolean value) {
        this.identifier = identifier;
        this.value = value;
    }

    public Variable(String identifier) {
        this(identifier, false);
    }

    public boolean evaluate() {
        return value;
    }

    public String toString() {
        return identifier + "";
    }
}

abstract class Operator implements Comparable<Operator> {

    final private int precedence;

    Operator(int precedence) {
        this.precedence = precedence;
    }


    public static BinaryOperator createFromString(String op) throws ParseException {
        String temp = op.toLowerCase();
        if (temp.equals("or")) {
            return new Or();
        } else if (temp.equals("and")) {
            return new And();
        } else if (temp.equals("xor")) {
            return new Xor();
        } else if (temp.equals("or")) {
            return new Or();
        } else if (temp.equals("implies")) {
            return new Implies();
        } else if (temp.equals("iff")) {
            return new Iff();
        }
        throw new ParseException("String " + op + " does not match any existing operator", 0);
    }

    public int compareTo(Operator other) {
        return Integer.compare(precedence, other.precedence);
    }

    public String toString() {
        String className = this.getClass().toString();
        String[] dotSeparated = className.split("\\.");
        if (dotSeparated.length > 0) {
            return dotSeparated[dotSeparated.length - 1];
        }
        return className;
    }
}

abstract class BinaryOperator extends Operator {

    BinaryOperator(int precedence) {
        super(precedence);
    }

    public abstract boolean operate(boolean x, boolean y);
}

class Not extends Operator {

    Not(int precedence) {
        super(precedence);
    }

    Not() {
        this(5);
    }

    public boolean operate(boolean in) {
        return !in;
    }
}

class Or extends BinaryOperator {

    Or(int precedence) {
        super(precedence);
    }

    Or() {
        this(3);
    }

    public boolean operate(boolean x, boolean y) {
        return x || y;
    }
}

class And extends BinaryOperator {

    And(int precedence) {
        super(precedence);
    }

    And() {
        this(4);
    }

    public boolean operate(boolean x, boolean y) {
        return x && y;
    }
}

class Xor extends BinaryOperator {

    Xor(int precedence) {
        super(precedence);
    }

    Xor() {
        this(2);
    }

    public boolean operate(boolean x, boolean y) {
        return x ^ y;
    }
}

class Implies extends BinaryOperator {

    Implies(int precedence) {
        super(precedence);
    }

    Implies() {
        this(1);
    }

    public boolean operate(boolean x, boolean y) {
        return !(x && !y);
    }
}

class Iff extends BinaryOperator {

    Iff(int precedence) {
        super(precedence);
    }

    Iff() {
        this(0);
    }

    public boolean operate(boolean x, boolean y) {
        return x == y;
    }
}