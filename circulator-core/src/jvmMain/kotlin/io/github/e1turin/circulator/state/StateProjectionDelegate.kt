package io.github.e1turin.circulator.state

import io.github.e1turin.circulator.model.Model
import java.lang.foreign.ValueLayout
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

public interface StateProjection<T> {
    public fun getProjectionValueOf(stateful: Stateful): T
    public fun setProjectionOf(stateful: Stateful, value: T)
}

public interface StateProjectionDelegate<T> : StateProjection<T>, ReadWriteProperty<Stateful, T> {
    override fun getValue(thisRef: Stateful, property: KProperty<*>): T {
        return getProjectionValueOf(thisRef)
    }

    override fun setValue(thisRef: Stateful, property: KProperty<*>, value: T) {
        setProjectionOf(thisRef, value)
    }
}

public interface StateProjectionDelegateProvider<T> : StateProjectionDelegate<T>,
    PropertyDelegateProvider<Stateful, StateProjectionDelegate<T>>

@PublishedApi
internal class ByteStateProjection(private val offset: Long) : StateProjectionDelegate<Byte> {
    override fun getProjectionValueOf(stateful: Stateful): Byte {
        return stateful.state.get(ValueLayout.JAVA_BYTE, offset)
    }

    override fun setProjectionOf(stateful: Stateful, value: Byte) {
        stateful.state.set(ValueLayout.JAVA_BYTE, offset, value)
    }
}

@PublishedApi
internal class UByteStateProjection(private val offset: Long) : StateProjectionDelegate<UByte> {
    override fun getProjectionValueOf(stateful: Stateful): UByte {
        return stateful.state.get(ValueLayout.JAVA_BYTE, offset).toUByte()
    }

    override fun setProjectionOf(stateful: Stateful, value: UByte) {
        stateful.state.set(ValueLayout.JAVA_BYTE, offset, value.toByte())
    }
}

@PublishedApi
internal class IntStateProjection(private val offset: Long) : StateProjectionDelegate<Int> {
    override fun getProjectionValueOf(stateful: Stateful): Int {
        return stateful.state.get(ValueLayout.JAVA_INT, offset)
    }

    override fun setProjectionOf(stateful: Stateful, value: Int) {
        stateful.state.set(ValueLayout.JAVA_INT, offset, value)
    }
}

@PublishedApi
internal class UIntStateProjection(private val offset: Long) : StateProjectionDelegate<UInt> {
    override fun getProjectionValueOf(stateful: Stateful): UInt {
        return stateful.state.get(ValueLayout.JAVA_INT, offset).toUInt()
    }

    override fun setProjectionOf(stateful: Stateful, value: UInt) {
        stateful.state.set(ValueLayout.JAVA_INT, offset, value.toInt())
    }
}

@PublishedApi
internal class LongStateProjection(private val offset: Long) : StateProjectionDelegate<Long> {
    override fun getProjectionValueOf(stateful: Stateful): Long {
        return stateful.state.get(ValueLayout.JAVA_LONG, offset)
    }

    override fun setProjectionOf(stateful: Stateful, value: Long) {
        stateful.state.set(ValueLayout.JAVA_LONG, offset, value)
    }
}

@PublishedApi
internal class ULongStateProjection(private val offset: Long) : StateProjectionDelegate<ULong> {
    override fun getProjectionValueOf(stateful: Stateful): ULong {
        return stateful.state.get(ValueLayout.JAVA_LONG, offset).toULong()
    }

    override fun setProjectionOf(stateful: Stateful, value: ULong) {
        stateful.state.set(ValueLayout.JAVA_LONG, offset, value.toLong())
    }
}

// TODO: Other type delegates

@PublishedApi
internal fun scaledOffset(offset: Long, byteSize: Long): Long {
    require(offset % byteSize == 0L) {
        "Accessed bytes should be aligned according to underlying data type size"
    }
    return offset / byteSize
}

public inline fun <reified T> Model.stateProjection(
    offset: Long,
    projectionType: StateProjectionType
): StateProjectionDelegate<T> {
    require(offset < numStateBytes) { "Offset $offset bounded to state size $numStateBytes" }

    val clazz = T::class

    val byteSize: Long
    @Suppress("UNCHECKED_CAST")
    val property = when (clazz) {
        Byte::class -> {
            byteSize = ValueLayout.JAVA_BYTE.byteSize()
            ByteStateProjection(scaledOffset(offset, byteSize))
        }

        UByte::class -> {
            byteSize = ValueLayout.JAVA_BYTE.byteSize()
            UByteStateProjection(scaledOffset(offset, byteSize))
        }

        Int::class -> {
            byteSize = ValueLayout.JAVA_INT.byteSize()
            IntStateProjection(scaledOffset(offset, byteSize))
        }

        UInt::class -> {
            byteSize = ValueLayout.JAVA_INT.byteSize()
            UIntStateProjection(scaledOffset(offset, byteSize))
        }

        Long::class -> {
            byteSize = ValueLayout.JAVA_LONG.byteSize()
            LongStateProjection(scaledOffset(offset, byteSize))
        }

        ULong::class -> {
            byteSize = ValueLayout.JAVA_LONG.byteSize()
            ULongStateProjection(scaledOffset(offset, byteSize))
        }

        else -> throw NotImplementedError("Unsupported Type for memory access: ${T::class.qualifiedName}")
    } as StateProjectionDelegate<T>

    require(offset + byteSize in 1..numStateBytes) {
        "Accessed memory - $byteSize bytes for ${clazz.qualifiedName} (${projectionType.name}) on offset $offset -" +
            " must be in bounds of state size $numStateBytes but it is not!"
    }

    return property
}

public inline fun <reified T> Model.input(offset: Long) = stateProjection<T>(offset, StateProjectionType.INPUT)
public inline fun <reified T> Model.output(offset: Long) = stateProjection<T>(offset, StateProjectionType.OUTPUT)
public inline fun <reified T> Model.register(offset: Long) = stateProjection<T>(offset, StateProjectionType.REGISTER)
public inline fun <reified T> Model.memory(offset: Long, layout: MemoryLayoutBuilder.()->MemoryLayout) = stateProjection<T>(offset, StateProjectionType.MEMORY).also { MemoryLayoutBuilder.layout() }
public inline fun <reified T> Model.wire(offset: Long) = stateProjection<T>(offset, StateProjectionType.WIRE)

