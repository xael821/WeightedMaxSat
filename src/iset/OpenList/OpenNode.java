package iset.OpenList;

/**
 * Created by aellison on 11/23/2016.
 */

public class OpenNode<E> {

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


    public void addNode(OpenNode<E> n) {
        OpenNode<E> temp = hasNext ? this.next : null;
        boolean nextnextExists = hasNext;
        hasNext = true;
        this.next = n;
        n.next.next = temp;
        n.next.hasNext = nextnextExists;
    }

    public OpenNode<E> tail() {
        if (hasNext) {
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


    void add(E[] arr, int index) {
        if (index >= arr.length) {
            return;
        }
        createNext(arr[index]);
        next.add(arr, index + 1);
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
                return new OpenNode[]{this, out};
            }
            if (hasNext) {
                return next.removeFromSuccessors(e);
            }
        }
        return new OpenNode[]{this, out};
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