package iset.RestoreList;

import java.util.*;

/**
 * Created by aellison on 11/25/2016.
 * <p>
 * once this is working in the iset algorithm with get(int) optimize by creating publically available 'access points'
 * that let you access certain nodes in an API ish way
 */
public class RestoreList<E> {

    private LinkedList<HashMap<RLNode<E>, RLNode<E>>> restoreStack;
    private LinkedList<Integer> txnSizeStack;
    private HashMap<RLNode<E>, RLNode<E>> currentTransaction;
    private RLNode<E> dummy = new RLNode<E>();
    private int size;
    private int currentTxnSize = 0;

    public static void main(String[] args) {
        int n = 10;
        Integer[] arr = new Integer[n];
        for (int i = 0; i < n; i++) {
            arr[i] = i;
        }
        RestoreList<Integer> L = new RestoreList<>(arr);
        L.print();
        L.openTransaction();
        L.remove(0);
        L.remove(2);
        L.closeTransaction();
        L.print();
        L.openTransaction();
        L.remove(3);
        L.remove(5);
        L.remove(4);

        L.closeTransaction();
        L.print();
        L.rollback();
        L.print();
        L.rollback();
        L.print();
    }

    void print(){
        System.out.print("size = "+size+": ");
        RLNode<E> temp = dummy.next;
        while(temp!=null){
            System.out.print(temp.value+" ");
            temp = temp.next;
        }
        System.out.println();
    }

    public RestoreList(){
        restoreStack = new LinkedList<>();
        currentTransaction = new HashMap<>();
        txnSizeStack = new LinkedList<>();
    }

    public RestoreList(E[] arr) {
        this();
        size = arr.length;
        RLNode<E> temp = dummy;
        for (int i = 0; i < arr.length; i++) {
            temp.next = new RLNode<E>(arr[i]);
            temp = temp.next;
        }
    }

    public void openTransaction(){
        currentTxnSize = 0;
    }

    //currentTransaction is pushed onto the stack, currentTransaction set to a new, empty map
    public void closeTransaction() {
        txnSizeStack.push(currentTxnSize);
    }

    public void remove(E target) {
        //this is the node that pointed to the node that just got removed
        RemovalPair<E> removed = dummy.removeFromSuccessors(target);
        currentTransaction.put(removed.n1, removed.n2);
        restoreStack.push(currentTransaction);
        currentTransaction = new HashMap<>(1);
        currentTxnSize++;
        size--;
    }

    public void rollback(){
        restore(txnSizeStack.pop());
    }

    //rolls back the last transaction
    void restore() {
        HashMap<RLNode<E>, RLNode<E>> transaction = restoreStack.pop();
        for (RLNode<E> key : transaction.keySet()) {
            key.insertNext(transaction.get(key));
        }
        size++;
    }

    void restore(int n){
        for(int i = 0; i<n && !restoreStack.isEmpty(); i++){
            restore();
        }
    }

}

class RLNode<E> {
    RLNode<E> next = null;
    E value = null;

    public RLNode() {
    }

    public RLNode(E e) {
        value = e;
    }

    void insertNext(RLNode<E> node) {
        node.next = this.next;
        this.next = node;
    }

    RemovalPair<E> removeFromSuccessors(E e) {
        if (next != null) {
            if (next.value == e) {
                //need to remove next from list. By default, we can set this node's next to null...
                RLNode<E> newNext = null;
                if (next.next != null) {
                    //... and if a new next exists, then we make it that
                    newNext = next.next;
                }
                RLNode<E> removed = this.next;
                this.next = newNext;
                return new RemovalPair<E>(this, removed);
            } else {
                return next.removeFromSuccessors(e);
            }
        }
        return null;
    }

}

class RemovalPair<E> {
    RLNode<E> n1, n2;

    public RemovalPair(RLNode<E> n1, RLNode<E> n2) {
        this.n1 = n1;
        this.n2 = n2;
    }
}
