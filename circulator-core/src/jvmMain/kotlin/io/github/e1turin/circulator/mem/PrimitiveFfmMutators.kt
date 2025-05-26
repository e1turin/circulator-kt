package io.github.e1turin.circulator.mem

import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout


public object ByteFfmMutator : FfmStateMutator<Byte>, FfmStateViewer<Byte> {
    override val byteSize: Long = ValueLayout.JAVA_BYTE.byteSize()
    override fun getValue(state: MemorySegment, offset: Long): Byte {
        return state.get(ValueLayout.JAVA_BYTE, offset)
    }

    override fun setValue(state: MemorySegment, offset: Long, value: Byte) {
        state.set(ValueLayout.JAVA_BYTE, offset, value)
    }
}

public object ShortFfmMutator : FfmStateMutator<Short>, FfmStateViewer<Short> {
    override val byteSize: Long = ValueLayout.JAVA_SHORT.byteSize()
    public override fun getValue(state: MemorySegment, offset: Long): Short {
        return state.get(ValueLayout.JAVA_SHORT, offset)
    }

    public override fun setValue(state: MemorySegment, offset: Long, value: Short) {
        state.set(ValueLayout.JAVA_SHORT, offset, value)
    }
}

public object IntFfmMutator : FfmStateMutator<Int>, FfmStateViewer<Int> {
    override val byteSize: Long = ValueLayout.JAVA_INT.byteSize()
    public override fun getValue(state: MemorySegment, offset: Long): Int {
        return state.get(ValueLayout.JAVA_INT, offset)
    }

    public override fun setValue(state: MemorySegment, offset: Long, value: Int) {
        state.set(ValueLayout.JAVA_INT, offset, value)
    }
}

public object LongFfmMutator : FfmStateMutator<Long>, FfmStateViewer<Long> {
    override val byteSize: Long = ValueLayout.JAVA_LONG.byteSize()
    public override fun getValue(state: MemorySegment, offset: Long): Long {
        return state.get(ValueLayout.JAVA_LONG, offset)
    }

    public override fun setValue(state: MemorySegment, offset: Long, value: Long) {
        state.set(ValueLayout.JAVA_LONG, offset, value)
    }
}

public object BooleanFfmMutator : FfmStateMutator<Boolean>, FfmStateViewer<Boolean> {
    override val byteSize: Long = ValueLayout.JAVA_BOOLEAN.byteSize()
    public override fun getValue(state: MemorySegment, offset: Long): Boolean {
        return state.get(ValueLayout.JAVA_BOOLEAN, offset)
    }

    public override fun setValue(state: MemorySegment, offset: Long, value: Boolean) {
        state.set(ValueLayout.JAVA_BOOLEAN, offset, value)
    }
}

public object FloatFfmMutator : FfmStateMutator<Float>, FfmStateViewer<Float> {
    override val byteSize: Long = ValueLayout.JAVA_FLOAT.byteSize()
    public override fun getValue(state: MemorySegment, offset: Long): Float {
        return state.get(ValueLayout.JAVA_FLOAT, offset)
    }

    public override fun setValue(state: MemorySegment, offset: Long, value: Float) {
        state.set(ValueLayout.JAVA_FLOAT, offset, value)
    }
}

public object DoubleFfmMutator : FfmStateMutator<Double>, FfmStateViewer<Double> {
    override val byteSize: Long = ValueLayout.JAVA_DOUBLE.byteSize()
    public override fun getValue(state: MemorySegment, offset: Long): Double {
        return state.get(ValueLayout.JAVA_DOUBLE, offset)
    }

    public override fun setValue(state: MemorySegment, offset: Long, value: Double) {
        state.set(ValueLayout.JAVA_DOUBLE, offset, value)
    }
}

public object CharFfmMutator : FfmStateMutator<Char>, FfmStateViewer<Char> {
    override val byteSize: Long = ValueLayout.JAVA_CHAR.byteSize()
    public override fun getValue(state: MemorySegment, offset: Long): Char {
        return state.get(ValueLayout.JAVA_CHAR, offset)
    }

    public override fun setValue(state: MemorySegment, offset: Long, value: Char) {
        state.set(ValueLayout.JAVA_CHAR, offset, value)
    }
}
