package io.github.e1turin.circulator.dsl

import io.github.e1turin.circulator.mem.FfmStateMutator


public class SizedMemoryConfig<T>(
    public val accessor: FfmStateMutator<T>,
    public val size: Long
)

public data class PlacedMemoryConfig<T> (
    public val accessor: FfmStateMutator<T>,
    public val size: Long,
    public val offset: Long
)

public interface MemoryBuilder: SignalBuilder {

    public infix fun <T> PlacedSignalConfig<T>.size(size: Long): PlacedMemoryConfig<T> {
        require(size > 0) { "Memory size must be positive, got $size" }
        return PlacedMemoryConfig(accessor, size, offset)
    }

    public infix fun <T> SizedMemoryConfig<T>.offset(offset: Long): PlacedMemoryConfig<T> {
        require(offset >= 0) { "Memory start offset must be not negative, got $offset" }
        return PlacedMemoryConfig(accessor, size, offset)
    }

    public operator fun <T> Int.times(signalConfig: SignalConfig<T>): SizedMemoryConfig<T> {
        require(this > 0) { "Memory size must positive, got $this" }
        return SizedMemoryConfig(signalConfig.accessor, this.toLong())
    }

    public operator fun <T> Long.times(signalConfig: SignalConfig<T>): SizedMemoryConfig<T> {
        require(this > 0) { "Memory size must positive, got $this" }
        return SizedMemoryConfig(signalConfig.accessor, this)
    }

    public operator fun <T> SignalConfig<T>.times(size: Int): SizedMemoryConfig<T> {
        require(size > 0) { "Memory size must positive, got $size" }
        return SizedMemoryConfig(accessor, size.toLong())
    }

    public operator fun <T> SignalConfig<T>.times(size: Long): SizedMemoryConfig<T> {
        require(size > 0) { "Memory size must positive, got $size" }
        return SizedMemoryConfig(accessor, size)
    }

    public companion object: MemoryBuilder
}
