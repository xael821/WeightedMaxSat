package iset.RestoreList;

import java.util.Iterator;

public class RLIterator<E> implements Iterator<E> {
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

    RLIterator(RestoreList list, RLHandle<E> start) {
        this(list);
        skipToStart(start);
    }

    private void skipToStart(RLHandle<E> start) {
        RLNode<E> old = null;

        if (start != null && start.rlnode != null) {
            while (hasNext()) {
                old = current;
                next();
                if( current!= null && current.value==start.rlnode.value){
                    break;
                }

            }
            if(old==null){
                System.out.println("skip error");
            }
            current = old;
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

    public RLHandle<E> handle() {
        return new RLHandle<E>(current);
    }

}