package io.github.e1turin.circulator.state

/**
 * Interface representing an object that has a state.
 *
 * @param M Type of underlying state.
 */
public interface Stateful<M> {
    public val state: M
}
