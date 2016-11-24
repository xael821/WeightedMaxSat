package iset.OpenList;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by aellison on 11/23/2016.
 */
public class OpenIterator<E> implements Iterator<E> {
    OpenList<E> list;
    OpenNode<E> current;
    public OpenIterator(OpenList<E> list){
        this.list=list;
        current = list.head.next();
    }
    public boolean hasNext(){
        return current.hasNext();
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements
     */
   public E next(){
       E out= current.value();
       current = current.next();
       return out;
   }
}
