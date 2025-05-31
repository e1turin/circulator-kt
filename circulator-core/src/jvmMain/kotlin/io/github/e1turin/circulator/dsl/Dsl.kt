package io.github.e1turin.circulator.dsl

import io.github.e1turin.circulator.mem.mutatorOf
import io.github.e1turin.circulator.state.*
import io.github.e1turin.circulator.types.MutableMemory
import io.github.e1turin.circulator.types.Memory
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty


public inline fun <reified T> signalOf(): SignalConfig<T> = SignalConfig(mutatorOf<T>())

public inline fun <T> FfmStateful.input(
    config: SignalBuilder.() -> PlacedSignalConfig<T>
): ReadWriteProperty<FfmStateful, T> = delegateFrom(SignalBuilder.config())

public inline fun <T> FfmStateful.output(
    config: SignalBuilder.() -> PlacedSignalConfig<T>
): ReadOnlyProperty<FfmStateful, T> = delegateFrom(SignalBuilder.config())

public inline fun <T> FfmStateful.wire(
    config: SignalBuilder.() -> PlacedSignalConfig<T>
): ReadOnlyProperty<FfmStateful, T> = delegateFrom(SignalBuilder.config())

public inline fun <T> FfmStateful.debugWire(
    config: SignalBuilder.() -> PlacedSignalConfig<T>
): ReadWriteProperty<FfmStateful, T> = delegateFrom(SignalBuilder.config())

public inline fun <T> FfmStateful.register(
    config: SignalBuilder.() -> PlacedSignalConfig<T>
): ReadOnlyProperty<FfmStateful, T> = delegateFrom(SignalBuilder.config())

public inline fun <T> FfmStateful.debugRegister(
    config: SignalBuilder.() -> PlacedSignalConfig<T>
): ReadWriteProperty<FfmStateful, T> = delegateFrom(SignalBuilder.config())

public inline fun <T> FfmStateful.memory(
    config: MemoryBuilder.() -> PlacedMemoryConfig<T>
): ReadOnlyProperty<FfmStateful, Memory<T>> = mutableMemory(config)

public inline fun <T> FfmStateful.mutableMemory(
    config: MemoryBuilder.() -> PlacedMemoryConfig<T>
): ReadOnlyProperty<FfmStateful, MutableMemory<T>> = delegateFrom(MemoryBuilder.config())


@PublishedApi
internal fun <T> FfmStateful.delegateFrom(config: PlacedSignalConfig<T>): FfmStateProjectionReadWriteDelegate<T> {
    val offset = scaledOffset(config.offset, config.accessor.byteSize)
    return FfmStateProjectionReadWriteDelegateImpl(config.accessor, offset)
}

@PublishedApi
internal fun <T> FfmStateful.delegateFrom(config: PlacedMemoryConfig<T>): FfmStateProjectionReadOnlyDelegate<MutableMemory<T>> {
    val offset = scaledOffset(config.offset, config.accessor.byteSize)
    return FfmMemoryStateDelegate(config.accessor, offset, config.size)
}

@PublishedApi
internal fun scaledOffset(offset: Long, byteSize: Long): Long {
    require(offset % byteSize == 0L) {
        "Accessed bytes should be aligned according to underlying data type size"
    }
    return offset / byteSize
}
