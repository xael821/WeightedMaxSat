package main;

/**
 * Created by aellison on 11/18/2016.
 */
import java.util.*;

public class Main {
    int N = 2;
    boolean[] arr = new boolean[N];
    ArrayList<Integer> ints = new ArrayList<Integer>();
    public static void main(String[] args) {
        Main m = new Main();
        m.test();
    }

    void test(){
        solve(N-1, true);
        solve(N-1, false);
        System.out.println(ints.size());
    }

    void solve(int depth, boolean value) {

        boolean orig = arr[depth];

        arr[depth] = value;
        if(value || depth==0) {
            print();
        }
        if (depth > 0) {
            solve(depth - 1, true);
            solve(depth - 1, false);
        }
        arr[depth] = orig;
    }

    void print() {
        int out = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i]) {
                out += 1 << i;
            }
            System.out.print(arr[i] + " ");
        }
        ints.add(out);
       System.out.print(out + "\n");
    }

}
