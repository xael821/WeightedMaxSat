package iset.RestoreList;

/**
 * Created by aellison on 11/26/2016.
 */
public class RLHandle<E> {

    RLNode<E> rlnode;

    public RLHandle(RLNode<E> n){
        rlnode = n;
    }

    @Override
    public boolean equals(Object o){
        RLHandle<E> other = (RLHandle<E>)o;
        if(rlnode.value == null || other.rlnode.value == null){
            return false;
        }
        return rlnode.value.equals(other.rlnode.value);
    }
}
