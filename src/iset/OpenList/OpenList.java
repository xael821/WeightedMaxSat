package iset.OpenList;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by aellison on 11/22/2016.
 * <p>
 * This file should be in its own package, separate from any classes that use it.
 */
public class OpenList<E> implements Iterable<E>{

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


    public OpenList(E[] arr){
        this();
        size = arr.length;
        head.add(arr,0);
    }

    public void add(E e) {
        head.add(e);
        size++;
    }

    /*
        returns the node containing the target to remove, and null if size = 0 or if target not in list.
     */

    public OpenNode<E>[] remove(E target){
        int size = size();
        if (size > 0) {
            size--;
            return head.removeFromSuccessors(target);
        }
        return null;
    }

    public void print() {
        head.print();
    }


  //  public int size(){
    //    return size;
    //}

    public E getIndex(int index){
        OpenNode<E> temp = head;
        //dummy is at -1 effectively
        index++;
        while(temp.hasNext() && index>0){
            temp = temp.next();
            index--;
        }
        return temp.value();
    }

    public int size(){
        int out = 0;
        OpenNode<E> temp  = head.next();
        while(temp!=null){
            out++;
            temp= temp.next();
        }
        return out;
    }

    public Iterator<E> iterator(){
        return new OpenIterator<E>(this);
    }
}
