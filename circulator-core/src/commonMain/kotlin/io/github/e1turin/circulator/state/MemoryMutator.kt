package io.github.e1turin.circulator.state

public interface MemoryViewer<M, T> {
    public fun getValue(memory: M, offset: Long): T
    public val byteSize: Long
}

public interface MemoryMutator<M, T> : MemoryViewer<M, T> {
    public fun setValue(memory: M, offset: Long, value: T)
}
