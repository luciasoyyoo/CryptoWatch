package cryptowatch.ui;

import cryptowatch.core.Observer;
import javax.swing.SwingUtilities;

public final class EdtObserver<E> implements Observer<E> {
    private final Observer<? super E> delegate;
    private EdtObserver(Observer<? super E> delegate){this.delegate=delegate;}
    public static <E> EdtObserver<E> of(Observer<? super E> delegate){return new EdtObserver<>(delegate);}
    @Override public void update(E event){
        if(SwingUtilities.isEventDispatchThread()) delegate.update(event);
        else SwingUtilities.invokeLater(()->delegate.update(event));
    }
}
