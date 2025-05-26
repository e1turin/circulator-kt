package io.github.e1turin.circulator.mem

import io.github.e1turin.circulator.types.Bit
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout


public object UByteFfmMutator : FfmStateMutator<UByte>, FfmStateViewer<UByte> {
    override val byteSize: Long = ValueLayout.JAVA_BYTE.byteSize()
    override fun getValue(state: MemorySegment, offset: Long): UByte {
        return state.get(ValueLayout.JAVA_BYTE, offset).toUByte()
    }

    override fun setValue(state: MemorySegment, offset: Long, value: UByte) {
        state.set(ValueLayout.JAVA_BYTE, offset, value.toByte())
    }
}

public object UShortFfmMutator : FfmStateMutator<UShort>, FfmStateViewer<UShort> {
    override val byteSize: Long = ValueLayout.JAVA_SHORT.byteSize()
    override fun getValue(state: MemorySegment, offset: Long): UShort {
        return state.get(ValueLayout.JAVA_SHORT, offset).toUShort()
    }

    override fun setValue(state: MemorySegment, offset: Long, value: UShort) {
        state.set(ValueLayout.JAVA_SHORT, offset, value.toShort())
    }
}

public object UIntFfmMutator : FfmStateMutator<UInt>, FfmStateViewer<UInt> {
    override val byteSize: Long = ValueLayout.JAVA_INT.byteSize()
    override fun getValue(state: MemorySegment, offset: Long): UInt {
        return state.get(ValueLayout.JAVA_INT, offset).toUInt()
    }

    override fun setValue(state: MemorySegment, offset: Long, value: UInt) {
        state.set(ValueLayout.JAVA_INT, offset, value.toInt())
    }
}

public object ULongFfmMutator : FfmStateMutator<ULong>, FfmStateViewer<ULong> {
    override val byteSize: Long = ValueLayout.JAVA_LONG.byteSize()
    override fun getValue(state: MemorySegment, offset: Long): ULong {
        return state.get(ValueLayout.JAVA_LONG, offset).toULong()
    }

    override fun setValue(state: MemorySegment, offset: Long, value: ULong) {
        state.set(ValueLayout.JAVA_LONG, offset, value.toLong())
    }
}

public object BitFfmMutator : FfmStateMutator<Bit>, FfmStateViewer<Bit> {
    override val byteSize: Long = ValueLayout.JAVA_BYTE.byteSize()
    override fun getValue(state: MemorySegment, offset: Long): Bit {
        return Bit(state.get(ValueLayout.JAVA_BYTE, offset))
    }

    override fun setValue(state: MemorySegment, offset: Long, value: Bit) {
        state.set(ValueLayout.JAVA_BYTE, offset, value.toByte())
    }
}
