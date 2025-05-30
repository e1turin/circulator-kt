package io.github.e1turin.circulator.dsl

import io.github.e1turin.circulator.mem.mutatorOf
import io.github.e1turin.circulator.model.Model
import io.github.e1turin.circulator.state.*
import io.github.e1turin.circulator.types.Memory
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty


public inline fun <reified T> signalOf(): SignalConfig<T> = SignalConfig(mutatorOf<T>())

@PublishedApi
internal fun <T> delegateFrom(signalConfig: PlacedSignalConfig<T>): FfmStateProjectionReadWriteDelegate<T> =
    FfmStateProjectionReadWriteDelegateImpl(signalConfig.accessor, signalConfig.offset)

public inline fun <T> input(signalConfig: SignalBuilder.() -> PlacedSignalConfig<T>): ReadWriteProperty<FfmStateful, T> =
    delegateFrom(SignalBuilder.signalConfig())

public inline fun <T> output(signalConfig: SignalBuilder.() -> PlacedSignalConfig<T>): ReadOnlyProperty<FfmStateful, T> =
    delegateFrom(SignalBuilder.signalConfig())

public inline fun <T> wire(signalConfig: SignalBuilder.() -> PlacedSignalConfig<T>): ReadOnlyProperty<FfmStateful, T> =
    delegateFrom(SignalBuilder.signalConfig())

public inline fun <T> debugWire(signalConfig: SignalBuilder.() -> PlacedSignalConfig<T>): ReadWriteProperty<FfmStateful, T> =
    delegateFrom(SignalBuilder.signalConfig())

public inline fun <T> register(signalConfig: SignalBuilder.() -> PlacedSignalConfig<T>): ReadOnlyProperty<FfmStateful, T> =
    delegateFrom(SignalBuilder.signalConfig())

public inline fun <T> debugRegister(signalConfig: SignalBuilder.() -> PlacedSignalConfig<T>): ReadWriteProperty<FfmStateful, T> =
    delegateFrom(SignalBuilder.signalConfig())


@PublishedApi
internal fun <T> delegateFrom(config: PlacedMemoryConfig<T>): FfmStateProjectionReadOnlyDelegate<Memory<T>> =
    FfmMemoryStateDelegate(config.accessor, config.offset, config.size)

// TODO: Implement mutable memory
public inline fun <T> memory(
    memoryConfig: MemoryBuilder.() -> PlacedMemoryConfig<T>
): ReadOnlyProperty<FfmStateful, Memory<T>> {
    val config = MemoryBuilder.memoryConfig()
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
