package iset.OpenList;

/**
 * Created by aellison on 11/22/2016.
 *
 * This file should be in its own package, separate from any classes that use it.
 */
public class OpenList<E> {

    OpenNode<E> head;
    OpenNode<E> tail;
    int size = 0;

    public static void main(String[] args) {
        OpenList<Integer> list = new OpenList<>();
        for (int i = 0; i < 5; i++) {
            list.add(i);
        }
    }

    public OpenList() {
        head = new OpenNode<E>();
        tail = head;

    }

    public void add(E e) {
        if (size == 0) {
            head.add(e);
        } else if (size == 1) {
            head.add(e);
            tail = head.next();
        } else {
            tail.add(e);
        }
        size++;
    }
    /*
        returns the node containing the target to remove, and null if size = 0 or if target not in list.
     */
    public OpenNode<E> remove(E target) {
        if(size>0){
            if(head.value() == target){

            }else{
                return head.removeFromSuccessors(target);
            }
            size--;
        }
        return null;
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
    //methods for use in OpenList and OpenNode - package specific.
    boolean isSet() {
        return isSet;
    }

    OpenNode<E> next() {
        return next;
    }

    E value(){
        return value;
    }

    OpenNode<E> removeSelf(){
        isSet = false;
        return this;
    }

    /*  if the parameter is found in any node after this one, then it will be removed.
        returns the list node which contains the element to be removed, and removes that node from the list
        NOTE: if the item is not in the list, then it returns null
     */
    OpenNode<E> removeFromSuccessors(E e){
        OpenNode<E> out = null;
        if(hasNext) {
            if (next.value == e) {
                out = next;
                if (next.hasNext) {
                    next = next.next;
                } else {
                    setNextNull();
                }
                return out;
            }
            if (hasNext) {
                //return next.remove(e);
            }
        }
        return out;
    }
    //private, class internal methods
    private void createNext(E e) {
        next = new OpenNode<E>(e);
        hasNext = true;
    }

    private void setNextNull(){
        next = null;
        hasNext = false;
    }

}