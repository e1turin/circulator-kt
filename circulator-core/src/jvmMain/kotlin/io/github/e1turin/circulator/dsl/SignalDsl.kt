package io.github.e1turin.circulator.dsl

import io.github.e1turin.circulator.mem.FfmStateMutator


public class SignalConfig<T> @PublishedApi internal constructor(
    public val accessor: FfmStateMutator<T>
)

public class SizedSignalConfig<T> @PublishedApi internal constructor(
    public val accessor: FfmStateMutator<T>,
    public val bitOrder: ClosedRange<Int>
)

public class PlacedSignalConfig<T> @PublishedApi internal constructor(
    public val accessor: FfmStateMutator<T>,
    public val bitOrder: ClosedRange<Int>,
    public val offset: Long
)

public interface SignalBuilder {

    public infix fun <T> SignalConfig<T>.bits(bitOrder: ClosedRange<Int>): SizedSignalConfig<T> {
        require(bitOrder.start >= 0 && bitOrder.endInclusive >= 0) { "Bits order must not be negative but got $bitOrder" }
        return SizedSignalConfig(accessor, bitOrder)
    }

    public infix fun <T> SignalConfig<T>.bits(bitSize: Int): SizedSignalConfig<T> {
        require(bitSize > 0) { "Number of bits must be positive, got $bitSize" }
        return SizedSignalConfig(accessor, (bitSize - 1)..0) // Big-Endian
    }

    public infix fun <T> SignalConfig<T>.offset(offset: Long): PlacedSignalConfig<T> {
        require(offset >= 0) { "State offset must not be negative but got $offset" }
        val expectBitOrder = (accessor.byteSize.toInt() * 8 - 1)..0 // Big-Endian
        return PlacedSignalConfig(accessor, expectBitOrder, offset)
    }

    public infix fun <T> SizedSignalConfig<T>.offset(offset: Long): PlacedSignalConfig<T> {
        require(offset >= 0) { "State offset must not be negative but got $offset" }
        return PlacedSignalConfig(accessor, bitOrder, offset)
    }

    public companion object: SignalBuilder
}

