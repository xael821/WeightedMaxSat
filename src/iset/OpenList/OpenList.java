package iset.OpenList;

/**
 * Created by aellison on 11/22/2016.
 * <p>
 * This file should be in its own package, separate from any classes that use it.
 */
public class OpenList<E> {

    OpenNode<E> head;
    OpenNode<E> tail;
    int size = 0;

    public static void main(String[] args) {
        OpenList<Integer> list = new OpenList<>();
        for (int i = 0; i < 5; i++) {
            list.add(i );
        }
        OpenNode<Integer>[] r = list.remove(4);
        list.print();
        r[1].add(5);
        r[0].addNode(r[1]);

        list.print();
    }

    public OpenList() {
        head = new OpenNode<E>(null);
        tail = head;
    }

    public void add(E e) {
        head.add(e);
        size++;
    }

    /*
        returns the node containing the target to remove, and null if size = 0 or if target not in list.
     */

    private OpenNode<E>[] remove(E target){
        if (size > 0) {
            size--;
            return head.removeFromSuccessors(target);
        }
        return null;
    }

    public void print() {
        head.print();
    }

}

class OpenNode<E> {

    private E value;
    private OpenNode<E> next;
    private boolean isSet = false;
    private boolean hasNext = false;

    public OpenNode() {
    }

    public OpenNode(E e) {
        value = e;
        isSet = true;
    }

    public void add(E e) {
        if (isSet) {
            if (hasNext) {
                next.add(e);
            } else {
                createNext(e);
            }
        } else {
            value = e;
            isSet = true;
        }
    }

    public void addNode(OpenNode<E> n){
        OpenNode<E> temp = hasNext ? this.next: null;
        boolean nextnextExists = hasNext;
        hasNext = true;
        this.next = n;
        n.next.next=temp;
        n.next.hasNext=nextnextExists;
    }

    public OpenNode<E> tail(){
        if(hasNext){
            return next.tail();
        }
        return this;
    }

    public boolean hasNext() {
        return hasNext;
    }

    public void print() {
        System.out.println(value);
        if (hasNext) {
            next.print();
        }
    }

    //methods for use in OpenList and OpenNode - package specific.
    boolean isSet() {
        return isSet;
    }

    OpenNode<E> next() {
        return next;
    }

    E value() {
        return value;
    }

    OpenNode<E> removeSelf() {
        isSet = false;
        return this;
    }

    /*  if the parameter is found in any node after this one, then it will be removed.
        returns the list node which contains the element to be removed, and removes that node from the list
        NOTE: if the item is not in the list, then it returns null
     */
    OpenNode<E>[] removeFromSuccessors(E e) {
        OpenNode<E> out = null;
        if (hasNext) {
            if (next.value == e) {
                out = next;
                if (next.hasNext) {
                    next = next.next;
                } else {
                    setNextNull();
                }
                out.setNextNull();
                return new OpenNode[]{this,out};
            }
            if (hasNext) {
                return next.removeFromSuccessors(e);
            }
        }
        return new OpenNode[]{this,out};
    }

    //private, class internal methods
    private void createNext(E e) {
        next = new OpenNode<E>(e);
        hasNext = true;
    }

    private void setNextNull() {
        next = null;
        hasNext = false;
    }


}