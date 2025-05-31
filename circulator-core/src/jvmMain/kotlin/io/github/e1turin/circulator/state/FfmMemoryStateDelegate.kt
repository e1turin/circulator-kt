package io.github.e1turin.circulator.state

import io.github.e1turin.circulator.mem.FfmStateMutator
import io.github.e1turin.circulator.types.MutableMemory
import java.lang.foreign.MemorySegment
import kotlin.reflect.KProperty


public class FfmMemoryStateDelegate<T>(
    private val accessor: FfmStateMutator<T>,
    private val offset: Long,
    private val size: Long,
): FfmStateProjectionReadOnlyDelegate<MutableMemory<T>> {
    override fun getValue(thisRef: FfmStateful, property: KProperty<*>): MutableMemory<T> {
        return MutableMemoryImpl(thisRef.state, size)
    }

    private inner class MutableMemoryImpl(
        private val state: MemorySegment,
        override val size: Long
    ) : MutableMemory<T> {

        override fun get(index: Long): T {
            require(0 <= index && index < size) { "Index must be not negative and fit in memory size $size, got $index" }
            return accessor.getValue(state, offset + index)
        }

        override fun set(index: Long, value: T) {
            require(0 <= index && index < size) { "Index must be not negative and fit in memory size $size, got $index" }
            accessor.setValue(state, offset + index, value)
        }

        override fun set(data: List<T>) {
            check(size <= Int.MAX_VALUE) { "Memory size is out of Int value range, possible lost data" }
            require(data.size.toLong() == size) {
                val diff = data.size - size
                    "Only full memory can be overwritten. But got ${
                    if (diff < 0) {
                        "${-diff} elements less"
                    } else {
                        "$diff elements more"
                    }
                }"
            }

            // TODO: use effective copy
            repeat(size.toInt()) {
                set(it.toLong(), data[it])
            }
        }

        override fun toList(): List<T> {
            check(size <= Int.MAX_VALUE) { "Memory size is out of Int value range, possible lost data" }

            // TODO: use effective copy
            val list = List(size.toInt()) {
                get(it.toLong())
            }
            return list
        }
    }
}
