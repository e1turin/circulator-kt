package io.github.e1turin.circulator.dsl

import io.github.e1turin.circulator.mem.FfmStateMutator
import io.github.e1turin.circulator.mem.FfmStateViewer
import io.github.e1turin.circulator.mem.mutatorOf
import io.github.e1turin.circulator.mem.viewerOf
import io.github.e1turin.circulator.model.Model
import io.github.e1turin.circulator.state.*

public inline infix fun <reified T> FfmStateViewer<T>.at(offset: Long): StateProjectionReadOnlyDelegate<FfmStateful, T> {
    return FfmStateProjectionReadOnlyDelegateImpl(this, offset)
}

public inline infix fun <reified T> FfmStateMutator<T>.at(offset: Long): StateProjectionReadWriteDelegate<FfmStateful, T> {
    return FfmStateProjectionReadWriteDelegateImpl(this, offset)
}

public inline fun <reified T> signalOut(offset: Long): StateProjectionReadOnlyDelegate<FfmStateful, T> =
    FfmStateProjectionReadOnlyDelegateImpl(viewerOf<T>(), offset)

public inline fun <reified T> signalIn(offset: Long): StateProjectionReadWriteDelegate<FfmStateful, T> =
    FfmStateProjectionReadWriteDelegateImpl(mutatorOf<T>(), offset)


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
