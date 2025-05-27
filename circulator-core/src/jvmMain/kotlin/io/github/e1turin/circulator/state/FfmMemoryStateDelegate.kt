package io.github.e1turin.circulator.state

import io.github.e1turin.circulator.mem.FfmStateMutator
import io.github.e1turin.circulator.types.Memory
import java.lang.foreign.MemorySegment
import kotlin.reflect.KProperty


public class FfmMemoryStateDelegate<T>(
    private val accessor: FfmStateMutator<T>,
    private val offset: Long,
    private val stride: Long,
    private val depth: Long,
): FfmStateProjectionReadOnlyDelegate<Memory<T>> {
    override fun getValue(thisRef: FfmStateful, property: KProperty<*>): Memory<T> {
        return MemoryImpl(thisRef.state)
    }

    private inner class MemoryImpl(
        private val state: MemorySegment,
    ) : Memory<T> {
        override val stride: Long get() = this@FfmMemoryStateDelegate.stride
        override val depth: Long get() = this@FfmMemoryStateDelegate.depth

        override fun get(stride: Long, depth: Long): T {
            require(stride < this@FfmMemoryStateDelegate.stride && depth < this@FfmMemoryStateDelegate.depth)
            return accessor.getValue(state, calculatePosition(stride, depth))
        }

        override fun set(stride: Long, depth: Long, value: T) {
            require(stride < this@FfmMemoryStateDelegate.stride && depth < this@FfmMemoryStateDelegate.depth)
            accessor.setValue(state, calculatePosition(stride, depth), value)
        }

        override fun set(data: List<T>) {
            require(depth * stride == data.size.toLong()) {
                val diff = data.size - depth * stride
                    "Only full memory can be overwritten. But got ${
                    if (diff < 0) {
                        "${-diff} elements less"
                    } else {
                        "$diff elements more"
                    }
                }"
            }
            // TODO: use effective copy
            for (s in 0..<stride) {
                for (d in 0..<depth) {
                    set(s, d, data[(s * d).toInt()])
                }
            }
        }

        override fun toList(): List<T> {
            val list = ArrayList<T>((stride * depth).toInt())
            // TODO: use effective copy
            for (s in 0..<stride) {
                for (d in 0..<depth) {
                    list[(s * d).toInt()] = get(s, d)
                }
            }
            return list
        }

        private fun calculatePosition(stride: Long, depth: Long): Long {
            return offset + stride * depth * accessor.byteSize
        }
    }
}
