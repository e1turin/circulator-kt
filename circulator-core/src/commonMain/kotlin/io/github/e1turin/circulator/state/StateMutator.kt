package io.github.e1turin.circulator.state

public interface StateViewer<in M, out T> {
    public fun getValue(state: M, offset: Long): T
    public val byteSize: Long
}

public interface StateMutator<in M, T> : StateViewer<M, T> {
    public fun setValue(state: M, offset: Long, value: T)
}
