package iset;

//(c) Alex Ellison 2013
// A utility class for standard matrix operations
import java.util.*;

public class Matrices {
    // for keeping track of flops

    int ops = 0;
    /*
     * this hashmap is crucial in reducing the worst case scenario for a power
     * computation. Since the algorithm for an exponent of say, 15, would work
     * by computing A^8*A^4*A^2*A^1, in that order, it would compute A^8 first,
     * but in doing so it also computes A^4 and A^2, so rather than recomputing
     * those values recursively for the mother method, we can just use the
     * stored values. The index is the base2 log of the exponent (parameter of
     * pow2()). The method pow() must clear the table so another matrix B
     * wouldn't get an erroneous power computation.
     *
     * Note: previous is a Map, so it can be of any type really, just needs
     * to have the proper functions of associativity.
     */
    static Map<Integer, double[][]> previous = new HashMap<Integer, double[][]>();

    static public double[][] power(double[][] a, int exp) {
        //uses recursive factoring for fast exponentiation
        /*
         * takes the exponent and breaks it into the sum of the largest
         * possible powers of two- so 31 would break into 16+8+4+2+1.
         * Then it multiplies A raised to those "nice" exponents; that
         * raising is done in a seperate method, pow2(a). After looking at
         * the data, the worst case scenario is for any exponent 2^i-1 and best
         * for 2^i. O(n)=log(n)
         */
        if (a.length != a[0].length) {
            // raising matrices to a power only works if square
            return null;
        }
        double[][] out = I(a.length);

        int currentExp = 0;

        while (currentExp < exp) {
            int n = largestTwo(exp - currentExp);
            out = mult(out, pow2(a, log2(n)));
            currentExp += n;
        }
        previous.clear();
        return out;
    }

    static private double[][] pow2(double[][] a, int n) {
        //raises matrix a to the 2^n
        if (n == 0) {
            //2^0 = 1, return a^1=a
            return a;
        } else if (n == 1) {
            //2^1=2, return a^2
            return mult(a, a);
        }
        if (previous.containsKey(n)) {
            return previous.get(n);
        }
        //storing this is the critical time-cost saver
        double[][] temp = pow2(a, n - 1);
        double[][] out = mult(temp, temp);
        previous.put(new Integer(n), out);
        return out;
    }

    static public double[][] add(double[][] a, double[][] b) {
        //returns sum of two matrices, a and b of the same dimensions
        if (a.length != b.length || a[0].length != b[0].length) {
            return null;
        }
        double[][] out = new double[a.length][a[0].length];

        for (int i = 0; i < out.length; i++) {
            for (int e = 0; e < out[0].length; e++) {
                out[i][e] = a[i][e] + b[i][e];
            }
        }
        return out;
    }

    static public double[][] mult(double alpha, double[][] a) {
        //scalar multiplies alpha and a
        double out[][] = new double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int e = 0; e < a[0].length; e++) {
                out[i][e] = alpha * a[i][e];
            }
        }
        return out;
    }

    static public double[][] mult(double[][] a, double[][] b) {
        // returns a*b
        int m = a.length;
        int n = b[0].length;
        int k = b.length;

        double[][] out = new double[m][n];

        for (int i = 0; i < m; i++) {
            for (int e = 0; e < n; e++) {
                double scalar = 0;
                for (int j = 0; j < k; j++) {
                    scalar += a[i][j] * b[j][e];
                }
                out[i][e] = scalar;
            }
        }
        return out;
    }

    static public double[][] adjugate(double[][] a) {
        //computes adjugate/adjoint of matrix: flip everything over primary diag
        int m = a.length;
        int n = a[0].length;
        double[][] out = new double[n][m];
        for (int i = 0; i < m; i++) {
            for (int e = 0; e < n; e++) {
                out[e][i] = a[i][e];
            }
        }
        return out;
    }

    static public double[][] cofactor(double[][] a) {
        //computes cofactor of a (apply factor of +/- 1 to each a(i,j} in
        //checkerboard fashion so a(0,0) gets 1, and a(1,0) and a(0,1) get -1
        int m = a.length;
        int n = a[0].length;
        double[][] out = new double[m][n];
        for (int i = 0; i < m; i++) {
            for (int e = 0; e < n; e++) {
                if ((i + e) % 2 == 0) {
                    out[i][e] = a[i][e];
                } else {
                    out[i][e] = -a[i][e];
                }
            }
        }
        return out;
    }

    static public double[][] minor(double[][] a, int row, int col) {
        //computes a without row I col E
        int m = a.length;
        int n = a[0].length;

        double[][] out = new double[m - 1][n - 1];
        //above and left
        for (int i = 0; i < row; i++) {
            for (int e = 0; e < col; e++) {
                out[i][e] = a[i][e];
            }
        }
        //above and right
        for (int i = row + 1; i < m; i++) {
            for (int e = 0; e < col; e++) {
                out[i - 1][e] = a[i][e];
            }
        }
        //below and left
        for (int i = 0; i < row; i++) {
            for (int e = col + 1; e < n; e++) {
                out[i][e - 1] = a[i][e];
            }
        }
        //below and right
        for (int i = row + 1; i < m; i++) {
            for (int e = col + 1; e < n; e++) {
                out[i - 1][e - 1] = a[i][e];
            }
        }
        return out;
    }

    static double determinate(double[][] a) {
        /*
         * note this recursive function comoputes a determinate but does not
         * make use of dynamic programming and so has great room for
         * optimization, particularly for large a.
         */
        int out = 0;
        if (a.length != a[0].length) {
            System.out.println("dimension error");
            return 0;
        }
        if (a.length == 2) {
            return a[0][0] * a[1][1] - a[1][0] * a[0][1];
        }

        for (int i = 0; i < a.length; i++) {
            if (a[0][i] != 0) {
                //the above condition limits some unecessary computation
                out += Math.pow(-1, i) * a[0][i] * determinate(minor(a, 0, i));
            }
        }
        return out;
    }

    static public double[][] inverse(double[][] a) {
        /*
         * here we compute the inverse by computing a matrix where each a(i,j)
         * is the det of the minor of a w/ respect to i,j. Then we take its
         * cofactor, and then it's adjugate, then divide by a's determinate.
         * see:
         http://www.mathsisfun.com/algebra/matrix-inverse-minors-cofactors-adjugate.html
         */
        if (a.length != a[0].length) {
            System.out.println("dimension error");
            return null;
        }
        int m = a.length;
        double[][] out = new double[m][m];
        for (int i = 0; i < m; i++) {
            for (int e = 0; e < m; e++) {
                out[i][e] = determinate(minor(a, i, e));
            }
        }
        out = cofactor(out);
        out = adjugate(out);
        //out = mult(1 / determinate(a), out);
        return out;
    }

    static public double[][] I(int n) {
        // identity matrix of size n
        double[][] out = zero(n);
        for (int i = 0; i < n; i++) {
            out[i][i] = 1;
        }
        return out;
    }

    static public double[][] zero(int n) {
        // returns a zero matrix of size n
        double[][] out = new double[n][n];
        return out;
    }

    static public double[][] random(int m, int n) {
        return random(m, n, 10);
    }

    static public double[][] random(int m, int n, double maxMagnitude) {
        //generates a random matrix that's mxn with entries x: -10<x<10
        double[][] out = new double[m][n];
        for (int i = 0; i < m; i++) {
            for (int e = 0; e < n; e++) {
                out[i][e] = (2 * maxMagnitude * Math.random()) - maxMagnitude;
            }
        }
        return out;
    }

    static public double[][] copy(double[][] M) {
        double[][] out = new double[M.length][M[0].length];
        for (int i = 0; i < out.length; i++) {
            System.arraycopy(M[i], 0, out[i], 0, M[i].length);
        }
        return out;
    }

    static void elimRow(double[][] M, int row) {
        //assuming indexing is row then column
        for (int i = 0; i < M.length; i++) {
            M[row][i] = 0;
        }
    }

    static void elimCol(double[][] M, int col) {
        //assuming indexing is row then column
        for (int i = 0; i < M.length; i++) {
            M[i][col] = 0;
        }
    }

    static private int log2(int n) {
        // returns integer base 2 log of n
        int i = 0;
        while (n != 1) {
            n = n >> 1;
            i++;
        }
        return i;
    }

    static private int largestTwo(int upperBound) {
        // returns the largest power of two <= upperBound
        int out = 1;
        while (out < upperBound) {
            out = out << 1;
        }
        if (out > upperBound) {
            out = out >> 1;
        }
        return out;
    }

    static public String toString(int[][] a) {
        String out = "";
        for (int i = 0; i < a.length; i++) {
            for (int e = 0; e < a[0].length; e++) {
                out += a[i][e] + " ";
            }
            out += "\n";
        }
        return out;
    }

    static public String toString(double[][] a) {
        String out = "";
        for (int i = 0; i < a.length; i++) {
            for (int e = 0; e < a[0].length; e++) {
                out += a[i][e] + " ";
            }
            out += "\n";
        }
        return out;
    }

    static public void print(double[][] a) {
        System.out.println(toString(a));
    }

    static double[][] finite(double[][] a) {
        //returns binary truth of each a(i,j) being finite
        double[][] out = new double[a.length][a[0].length];
        for (int i = 0; i < out.length; i++) {
            for (int e = 0; e < out[0].length; e++) {
                if (Double.isFinite(a[i][e])) {
                    out[i][e] = 1;
                } else {
                    out[i][e] = 0;
                }
            }
        }
        return out;
    }

    static double[][] diagonalShift(double[][] a, int shift) {
        /*
         in the case of an adjacency matrix, this is effectively shifts each
         vertice's label by a specified number (modulo).
        */
        int n = a.length;
        int m = a[0].length;
        double[][] out = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int e = 0; e < m; e++) {
                out[i][e] = a[(i + shift) % n][(e + shift) % m];
            }
        }
        return out;
    }
}
