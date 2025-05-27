package io.github.e1turin.circulator.dsl

import io.github.e1turin.circulator.mem.FfmStateMutator


public class SizedMemoryConfig<T>(
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
    public operator fun <T> SignalConfig<T>.get(stride: Long, depth: Long): SizedMemoryConfig<T> {
        return SizedMemoryConfig(accessor, stride, depth)
    }
}
