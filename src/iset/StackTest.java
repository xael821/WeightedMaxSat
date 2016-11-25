package iset;

/**
 * Created by aellison on 11/25/2016.
 */


import java.util.*;
public class StackTest {
    //checking that addLast is O(1)... data was a little weird but it looks like it is.
    public static void main(String[] args) {
        for (int n = 1; n < 7; n++) {
            long time = System.currentTimeMillis();
            LinkedList<Integer> l = new LinkedList<>();
            for (int i = 0; i < 1000000<<n; i++) {
                l.addLast(i);
            }
            System.out.println((1000000<<n)+"\t"+(System.currentTimeMillis() - time));
        }
    }

}
