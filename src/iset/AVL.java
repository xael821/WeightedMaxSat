//(c) Alex Ellison 2014
/*
 * Balanced Binary Search Tree modeled after AVL trees, with the difference
 * that rather than using exact heights to determine balance factors, it uses
 * estimated heights based on size.
 *
 * Nov 2016:
 * I'm taking this and genericizing it, as well as adding successor()
 */
package iset;

public class AVL<E extends Comparable<? super E>> {

    AVL left, right;
    //IF YOU EDIT ME TAKE NOTE OF PROPER USE OF ASSIGNED
    boolean assigned;
    E data;
    int size = 0;

    public AVL() {
        assigned = false;
    }

    public static void main(String[] args){
        AVL<Double> tree= new AVL<>();
        for(int i = 0; i< 100;i ++){
            tree.add(Math.random());
        }
    }

    public AVL(E data) {
        assigned = true;
        this.data = data;
        left = right = null;
        size = 1;
    }

    public void add(E n) {
        size++;
        if (!assigned) {
            data = n;
            assigned = true;
            return;
        }
        if (n.compareTo( data)>=0) {
            if (right == null) {
                right = new AVL(n);
            } else {
                right.add(n);
            }
        } else {
            if (left == null) {
                left = new AVL(n);
            } else {
                left.add(n);
            }
        }
        balance();
    }

    //returns the value whose successor we're seeking if no such successor exists
    public E successor(E e){
        if(e.compareTo(data)<0){
            if(left!=null){
                return (E)left.successor(e);
            }else{
                return this.data;
            }
        }else{
            if(right!=null){
                return (E)right.successor(e);
            }else{
                return e;
            }
        }
    }

    public void balance() {
        /*
         * brief discussion on balancing: I don't have a great way of computing
         * heights, but in a full tree half the tree in the bottom level, so if
         * you compare sizes of trees with that in mind you can get a sense of
         * how their heights compare, too.
         */
        int thresh = size / 2;
        int balanceFactor = balanceFactor();
        //~~~~~~~~~~~~~~~~~~~~~~LEFT~~~~~~~~~~~~~~~~~~~~
        if (balanceFactor > 1 + thresh) {
            if (left.balanceFactor() < 0 - thresh) {
                //initialize values
                AVL temp, d, c;
                temp = d = c = null;
                temp = this.left;
                d = temp.right;
                if (d != null) {
                    c = d.left;
                }
                //swap
                this.left = temp.right;
                temp.right = c;
                d.left = temp;
            }
            //left right has would now have been converted to left left
            AVL temp = this.left;
            //swap root and left vals
            E tempVal = this.data;
            this.data = (E)temp.data;
            temp.data = tempVal;
            //delink temp from this
            this.left = temp.left;
            //reorder temp subtree
            temp.left = temp.right;
            //rejoin temp
            temp.right = this.right;
            this.right = temp;


        } else if (balanceFactor < -1 - thresh) {
            //~~~~~~~~~~~~~~~~~~~~~RIGHT~~~~~~~~~~~~~~~~~~~~~
            if (right.balanceFactor() > 0 + thresh) {
                //right left case
                //initialize values
                AVL temp, d, c = null;
                temp = this.right;
                d = temp.left;
                if (d != null) {
                    c = d.right;
                }
                //swap
                this.right = temp.left;
                temp.left = c;
                d.right = temp;
            }
            //left right has would now have been converted to right right
            AVL temp = this.right;
            //swap root and right vals
            E tempVal = this.data;
            this.data = (E)temp.data;
            temp.data = tempVal;
            //delink temp from this
            this.right = temp.right;
            //reorder temp subtree
            temp.right = temp.left;
            //rejoin temp
            temp.left = this.left;
            this.left = temp;
        }
    }

    public int height() {

        int leftH = 0;
        int rightH = 0;
        if (left != null) {
            leftH = left.height();
        }
        if (right != null) {
            rightH = right.height();
        }
        if (leftH > rightH) {
            return 1 + leftH;
        }
        return 1 + rightH;
    }

    public void print() {
        if (left != null) {
            left.print();
        }
        System.out.println(data);
        if (right != null) {
            right.print();
        }
    }

    public int balanceFactor() {
        //returns the left sub tree's height minus the right's
        int leftH = 0;
        if (left != null) {
            //   leftH = left.height();
            leftH = left.size;
        }
        int rightH = 0;
        if (right != null) {
            // rightH = right.height();
            rightH = right.size;
        }
        return leftH - rightH;
    }
}