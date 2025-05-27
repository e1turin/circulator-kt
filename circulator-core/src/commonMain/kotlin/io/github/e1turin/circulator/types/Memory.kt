package io.github.e1turin.circulator.types


public interface Memory<T> {
    public val depth: Long
    public val stride: Long

    public operator fun get(depth: Long, stride: Long): T
    public operator fun set(depth: Long, stride: Long, value: T)

    public fun set(data: List<T>)
    public fun toList(): List<T>
}


