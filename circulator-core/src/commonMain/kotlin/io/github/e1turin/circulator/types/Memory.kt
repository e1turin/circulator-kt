package io.github.e1turin.circulator.types


public interface ReadOnlyMemory<T> {
    public val size: Long

    public operator fun get(index: Long): T
    public fun toList(): List<T>
}

public interface Memory<T>: ReadOnlyMemory<T> {
    public operator fun set(index: Long, value: T)
    public fun set(data: List<T>)
}


