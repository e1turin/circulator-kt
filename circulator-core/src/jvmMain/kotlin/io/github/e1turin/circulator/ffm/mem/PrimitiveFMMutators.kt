package io.github.e1turin.circulator.ffm.mem

import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout


public object ByteFMMutator : ForeignMemoryMutator<Byte> {
    override val byteSize: Long = ValueLayout.JAVA_BYTE.byteSize()
    override fun getValue(memory: MemorySegment, offset: Long): Byte {
        return memory.get(ValueLayout.JAVA_BYTE, offset)
    }

    override fun setValue(memory: MemorySegment, offset: Long, value: Byte) {
        memory.set(ValueLayout.JAVA_BYTE, offset, value)
    }
}

public object ShortFMMutator : ForeignMemoryMutator<Short> {
    override val byteSize: Long = ValueLayout.JAVA_SHORT.byteSize()
    public override fun getValue(memory: MemorySegment, offset: Long): Short {
        return memory.get(ValueLayout.JAVA_SHORT, offset)
    }

    public override fun setValue(memory: MemorySegment, offset: Long, value: Short) {
        memory.set(ValueLayout.JAVA_SHORT, offset, value)
    }
}

public object IntFMMutator : ForeignMemoryMutator<Int> {
    override val byteSize: Long = ValueLayout.JAVA_INT.byteSize()
    public override fun getValue(memory: MemorySegment, offset: Long): Int {
        return memory.get(ValueLayout.JAVA_INT, offset)
    }

    public override fun setValue(memory: MemorySegment, offset: Long, value: Int) {
        memory.set(ValueLayout.JAVA_INT, offset, value)
    }
}

public object LongFMMutator : ForeignMemoryMutator<Long> {
    override val byteSize: Long = ValueLayout.JAVA_LONG.byteSize()
    public override fun getValue(memory: MemorySegment, offset: Long): Long {
        return memory.get(ValueLayout.JAVA_LONG, offset)
    }

    public override fun setValue(memory: MemorySegment, offset: Long, value: Long) {
        memory.set(ValueLayout.JAVA_LONG, offset, value)
    }
}

public object BooleanFMMutator : ForeignMemoryMutator<Boolean> {
    override val byteSize: Long = ValueLayout.JAVA_BOOLEAN.byteSize()
    public override fun getValue(memory: MemorySegment, offset: Long): Boolean {
        return memory.get(ValueLayout.JAVA_BOOLEAN, offset)
    }

    public override fun setValue(memory: MemorySegment, offset: Long, value: Boolean) {
        memory.set(ValueLayout.JAVA_BOOLEAN, offset, value)
    }
}

public object FloatFMMutator : ForeignMemoryMutator<Float> {
    override val byteSize: Long = ValueLayout.JAVA_FLOAT.byteSize()
    public override fun getValue(memory: MemorySegment, offset: Long): Float {
        return memory.get(ValueLayout.JAVA_FLOAT, offset)
    }

    public override fun setValue(memory: MemorySegment, offset: Long, value: Float) {
        memory.set(ValueLayout.JAVA_FLOAT, offset, value)
    }
}

public object DoubleFMMutator : ForeignMemoryMutator<Double> {
    override val byteSize: Long = ValueLayout.JAVA_DOUBLE.byteSize()
    public override fun getValue(memory: MemorySegment, offset: Long): Double {
        return memory.get(ValueLayout.JAVA_DOUBLE, offset)
    }

    public override fun setValue(memory: MemorySegment, offset: Long, value: Double) {
        memory.set(ValueLayout.JAVA_DOUBLE, offset, value)
    }
}

public object CharFMMutator : ForeignMemoryMutator<Char> {
    override val byteSize: Long = ValueLayout.JAVA_CHAR.byteSize()
    public override fun getValue(memory: MemorySegment, offset: Long): Char {
        return memory.get(ValueLayout.JAVA_CHAR, offset)
    }

    public override fun setValue(memory: MemorySegment, offset: Long, value: Char) {
        memory.set(ValueLayout.JAVA_CHAR, offset, value)
    }
}
