package io.github.e1turin.circulator.dsl

import io.github.e1turin.circulator.mem.FfmStateMutator
import io.github.e1turin.circulator.mem.FfmStateViewer
import io.github.e1turin.circulator.mem.mutatorOf
import io.github.e1turin.circulator.model.Model
import io.github.e1turin.circulator.state.*
import io.github.e1turin.circulator.types.Memory
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


public inline infix fun <reified T> FfmStateViewer<T>.at(offset: Long): StateProjectionReadOnlyDelegate<FfmStateful, T> {
    return FfmStateProjectionReadOnlyDelegateImpl(this, offset)
}

public inline infix fun <reified T> FfmStateMutator<T>.at(offset: Long): StateProjectionReadWriteDelegate<FfmStateful, T> {
    return FfmStateProjectionReadWriteDelegateImpl(this, offset)
}


public inline fun <reified T> signalOf(): SignalConfig<T> = SignalConfig(mutatorOf<T>())


@PublishedApi
internal fun <T> delegateFrom(config: PlacedSignalConfig<T>): FfmStateProjectionReadWriteDelegate<T> =
    FfmStateProjectionReadWriteDelegateImpl(config.accessor, config.offset)

public inline fun <T> input(config: () -> PlacedSignalConfig<T>): ReadWriteProperty<FfmStateful, T> =
    delegateFrom(config())

public inline fun <T> output(signal: () -> PlacedSignalConfig<T>): ReadOnlyProperty<FfmStateful, T> =
    delegateFrom(signal())

public inline fun <T> wire(signal: () -> PlacedSignalConfig<T>): ReadOnlyProperty<FfmStateful, T> =
    delegateFrom(signal())

public inline fun <T> debugWire(signal: () -> PlacedSignalConfig<T>): ReadWriteProperty<FfmStateful, T> =
    delegateFrom(signal())

public inline fun <T> register(signal: () -> PlacedSignalConfig<T>): ReadOnlyProperty<FfmStateful, T> =
    delegateFrom(signal())

public inline fun <T> debugRegister(signal: () -> PlacedSignalConfig<T>): ReadWriteProperty<FfmStateful, T> =
    delegateFrom(signal())


@PublishedApi
internal fun <T> delegateFrom(config: PlacedMemoryConfig<T>): FfmStateProjectionReadOnlyDelegate<Memory<T>> =
    object : FfmStateProjectionReadOnlyDelegate<Memory<T>> {
        val mem = memoryFrom(config)
        override fun getValue(thisRef: FfmStateful, property: KProperty<*>): Memory<T> = mem
    }

public inline fun <T> memory(
    accessor: MemoryBuilder.() -> PlacedMemoryConfig<T>
): ReadOnlyProperty<FfmStateful, Memory<T>> {
    val config = MemoryBuilder.accessor()
    val delegate = delegateFrom(config)
    return delegate
}


@PublishedApi
internal fun scaledOffset(offset: Long, byteSize: Long): Long {
    require(offset % byteSize == 0L) {
        "Accessed bytes should be aligned according to underlying data type size"
    }
    return offset / byteSize
}


public inline fun <reified T> Model.plainStateProjection(
    offset: Long,
    projectionType: StateProjectionType
): StateProjectionReadWriteDelegate<FfmStateful, T> {
    require(offset < numStateBytes) { "Offset $offset bounded to state size $numStateBytes" }

    val clazz = T::class
    val memoryAccessor = mutatorOf<T>()
    val byteSize: Long = memoryAccessor.byteSize

    require(offset + byteSize in 1..numStateBytes) {
        "Accessed memory - $byteSize bytes for ${clazz.qualifiedName} (${projectionType.name}) on offset $offset -" +
            " must be in bounds of state size $numStateBytes but it is not!"
    }

    return FfmStateProjectionReadWriteDelegateImpl(memoryAccessor, scaledOffset(offset, byteSize))
}


public inline fun <reified T> Model.input(offset: Long) = plainStateProjection<T>(offset, StateProjectionType.INPUT)
public inline fun <reified T> Model.output(offset: Long) = plainStateProjection<T>(offset, StateProjectionType.OUTPUT)
public inline fun <reified T> Model.register(offset: Long) = plainStateProjection<T>(offset, StateProjectionType.REGISTER)
//public inline fun <reified T> Model.memory(offset: Long, layout: MemoryLayoutBuilder.()->MemoryLayout) = plainStateProjection<T>(offset, _root_ide_package_.io.github.e1turin.circulator.state.StateProjectionType.MEMORY).also { MemoryLayoutBuilder.layout() }
public inline fun <reified T> Model.wire(offset: Long) = plainStateProjection<T>(offset, StateProjectionType.WIRE)
