package io.github.e1turin.circulator.types


public interface Memory<T> {
    public val stride: Long
    public val depth: Long

    public operator fun get(stride: Long, depth: Long): T
    public operator fun set(stride: Long, depth: Long, value: T)

    public fun set(data: List<T>)
    public fun toList(): List<T>
}


