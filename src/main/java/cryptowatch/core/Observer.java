package cryptowatch.core;

@FunctionalInterface
public interface Observer<E> {
    void update(E event);
}
