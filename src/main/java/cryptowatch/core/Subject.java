package cryptowatch.core;

public interface Subject<E> {
    void attach(Observer<? super E> observer);
    void detach(Observer<? super E> observer);
    void notifyObservers(E event);
}
