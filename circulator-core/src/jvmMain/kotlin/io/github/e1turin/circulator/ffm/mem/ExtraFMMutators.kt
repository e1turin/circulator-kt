package io.github.e1turin.circulator.ffm.mem

import io.github.e1turin.circulator.types.Bit
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout


public object UByteFMMutator : ForeignMemoryMutator<UByte> {
    override val byteSize: Long = ValueLayout.JAVA_BYTE.byteSize()
    override fun getValue(memory: MemorySegment, offset: Long): UByte {
        return memory.get(ValueLayout.JAVA_BYTE, offset).toUByte()
    }

    override fun setValue(memory: MemorySegment, offset: Long, value: UByte) {
        memory.set(ValueLayout.JAVA_BYTE, offset, value.toByte())
    }
}

public object UShortFMMutator : ForeignMemoryMutator<UShort> {
    override val byteSize: Long = ValueLayout.JAVA_SHORT.byteSize()
    override fun getValue(memory: MemorySegment, offset: Long): UShort {
        return memory.get(ValueLayout.JAVA_SHORT, offset).toUShort()
    }

    override fun setValue(memory: MemorySegment, offset: Long, value: UShort) {
        memory.set(ValueLayout.JAVA_SHORT, offset, value.toShort())
    }
}

public object UIntFMMutator : ForeignMemoryMutator<UInt> {
    override val byteSize: Long = ValueLayout.JAVA_INT.byteSize()
    override fun getValue(memory: MemorySegment, offset: Long): UInt {
        return memory.get(ValueLayout.JAVA_INT, offset).toUInt()
    }

    override fun setValue(memory: MemorySegment, offset: Long, value: UInt) {
        memory.set(ValueLayout.JAVA_INT, offset, value.toInt())
    }
}

public object ULongFMMutator : ForeignMemoryMutator<ULong> {
    override val byteSize: Long = ValueLayout.JAVA_LONG.byteSize()
    override fun getValue(memory: MemorySegment, offset: Long): ULong {
        return memory.get(ValueLayout.JAVA_LONG, offset).toULong()
    }

    override fun setValue(memory: MemorySegment, offset: Long, value: ULong) {
        memory.set(ValueLayout.JAVA_LONG, offset, value.toLong())
    }
}

public object BitFMMutator : ForeignMemoryMutator<Bit> {
    override val byteSize: Long = ValueLayout.JAVA_BYTE.byteSize()
    override fun getValue(memory: MemorySegment, offset: Long): Bit {
        return Bit(memory.get(ValueLayout.JAVA_BYTE, offset))
    }

    override fun setValue(memory: MemorySegment, offset: Long, value: Bit) {
        memory.set(ValueLayout.JAVA_BYTE, offset, value.toByte())
    }
}
