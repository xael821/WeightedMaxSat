package iset.RestoreList;

import java.util.*;

/**
 * Created by aellison on 11/25/2016.
 * <p>
 * once this is working in the iset algorithm with get(int) optimize by creating publically available 'access points'
 * that let you access certain nodes in an API ish way
 */
public class RestoreList<E> implements Iterable<E> {

    private LinkedList<HashMap<RLNode<E>, RLNode<E>>> restoreStack;
    private LinkedList<Integer> txnSizeStack;
    private HashMap<RLNode<E>, RLNode<E>> currentTransaction;
    //needs package access for iterator
    RLNode<E> dummy = new RLNode<E>();
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

    public void print() {
        System.out.print("size = " + size + ": ");
        RLNode<E> temp = dummy.next;
        while (temp != null) {
            System.out.print(temp.value + " ");
            temp = temp.next;
        }
        System.out.println();
    }

    public RestoreList() {
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

    public void openTransaction() {
        currentTxnSize = 0;
    }

    //currentTransaction is pushed onto the stack, currentTransaction set to a new, empty map
    public void closeTransaction() {
        txnSizeStack.push(currentTxnSize);
    }

    public void remove(E target) {
        //this is the node that pointed to the node that just got removed
        RemovalPair<E> removed = dummy.removeFromSuccessors(target);
        //don't try to remove if it's not in the list
        if(removed==null){
            return;
        }
        currentTransaction.put(removed.n1, removed.n2);
        restoreStack.push(currentTransaction);
        currentTransaction = new HashMap<>(1);
        currentTxnSize++;
        size--;
    }

    public void rollback() {
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

    void restore(int n) {
        for (int i = 0; i < n && !restoreStack.isEmpty(); i++) {
            restore();
        }
    }

    public E getIndex(int i) {
        return dummy.get(i + 1);
    }

    public int size() {
        return size;
    }

    public Iterator<E> iterator() {
        return new RLIterator<E>(this);
    }

    public Iterator<E> iterator(E start) {
        return new RLIterator<E>(this,start);
    }

    public ArrayList<E> intersection(List<E> other) {
        ArrayList<E> out = new ArrayList<E>(size);
        if (size > 0) {

            RLNode<E> temp = dummy.next;
            Iterator<E> I = other.iterator();

            while (I.hasNext()) {

            }
        }
        return out;
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

    E get(int i) {
        if (i == 0) {
            return value;
        }
        if (next != null) {
            return next.get(i - 1);
        }
        return null;
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

class RLIterator<E> implements Iterator<E> {
    RLNode<E> current;
    boolean invalid = false;

    RLIterator(RestoreList list) {
        if (list.dummy == null) {
            invalid = true;
        } else if (list.dummy.next == null) {
            invalid = true;
        } else {
            current = list.dummy.next;
        }
    }

    RLIterator(RestoreList list, E start){
        this(list);
        while(hasNext()){
            if(start==next()){
                break;
            }
        }
    }

    public E next() {
        E out = current.value;
        current = current.next;
        return out;
    }

    public boolean hasNext() {
        if (invalid) {
            return false;
        }
        return current != null;
    }

}