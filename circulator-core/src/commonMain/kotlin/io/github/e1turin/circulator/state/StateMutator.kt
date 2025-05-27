package io.github.e1turin.circulator.state

// TODO: add toArray(begin, end) method
public interface StateViewer<in M, out T> {
    public fun getValue(state: M, offset: Long): T
    public val byteSize: Long
}

public interface StateMutator<in M, T> : StateViewer<M, T> {
    public fun setValue(state: M, offset: Long, value: T)
}
