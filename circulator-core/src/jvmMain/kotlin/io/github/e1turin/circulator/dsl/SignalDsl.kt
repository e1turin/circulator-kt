package io.github.e1turin.circulator.dsl

import io.github.e1turin.circulator.mem.FfmStateMutator


public class SignalConfig<T>(public val accessor: FfmStateMutator<T>) {
    public infix fun bits(bitOrder: ClosedRange<Int>): SizedSignalConfig<T> {
        require(bitOrder.start >= 0 && bitOrder.endInclusive >= 0) { "Bits order must not be negative but got $bitOrder" }
        return SizedSignalConfig(accessor, bitOrder)
    }

    public infix fun offset(offset: Long): PlacedSignalConfig<T> {
        require(offset >= 0) { "State offset must not be negative but got $offset" }
        val expectBitOrder = accessor.byteSize.toInt() * 8..0
        return PlacedSignalConfig(accessor, expectBitOrder, offset)
    }
}

public class SizedSignalConfig<T>(public val accessor: FfmStateMutator<T>, public val bitOrder: ClosedRange<Int>) {
    public infix fun offset(offset: Long): PlacedSignalConfig<T> {
        require(offset >= 0) { "State offset must not be negative but got $offset" }
        return PlacedSignalConfig(accessor, bitOrder, offset)
    }
}

public class PlacedSignalConfig<T>(
    public val accessor: FfmStateMutator<T>,
    public val bitOrder: ClosedRange<Int>,
    public val offset: Long
)

