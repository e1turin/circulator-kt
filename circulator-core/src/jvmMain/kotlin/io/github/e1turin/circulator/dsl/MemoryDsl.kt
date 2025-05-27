package io.github.e1turin.circulator.dsl

import io.github.e1turin.circulator.mem.FfmStateMutator
import io.github.e1turin.circulator.types.Memory


public class MemoryConfig<T>(
    public val accessor: FfmStateMutator<T>,
    public val stride: Long,
    public val depth: Long
) {
    public infix fun offset(offset: Long): PlacedMemoryConfig<T> {
        return PlacedMemoryConfig(accessor, stride, depth, offset)
    }
}

public data class PlacedMemoryConfig<T> (
    public val accessor: FfmStateMutator<T>,
    public val stride: Long,
    public val depth: Long,
    public val offset: Long
)


public object MemoryBuilder {
    public operator fun <T> SignalConfig<T>.get(stride: Long, depth: Long): MemoryConfig<T> {
        return MemoryConfig(accessor, stride, depth)
    }
}

public fun <T> memoryFrom(config: PlacedMemoryConfig<T>): Memory<T> = object : Memory<T> {
    val offset = config.offset
    val accessor = config.accessor
    override val depth = config.depth
    override val stride = config.stride

    override fun get(depth: Long, stride: Long): T = TODO("Not yet implemented")

    override fun set(depth: Long, stride: Long, value: T) = TODO("Not yet implemented")

    override fun set(data: List<T>) = TODO("Not yet implemented")

    override fun toList(): List<T> = TODO("Not yet implemented")
}
