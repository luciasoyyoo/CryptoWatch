package cryptowatch.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractSubject<E> implements Subject<E> {
    private final List<Observer<? super E>> observers = new CopyOnWriteArrayList<>();

    @Override
    public void attach(Observer<? super E> observer) {
        if (observer != null && !observers.contains(observer)) observers.add(observer);
    }

    @Override
    public void detach(Observer<? super E> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(E event) {
        for (Observer<? super E> observer : observers) {
            try { observer.update(event); }
            catch (RuntimeException ex) {
                System.err.println("Error en observador: " + ex.getMessage());
            }
        }
    }

    public int observerCount() { return observers.size(); }
}
