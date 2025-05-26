package io.github.e1turin.circulator.ffm.state

import io.github.e1turin.circulator.ffm.mem.ForeignMemoryMutator
import io.github.e1turin.circulator.ffm.mem.accessorOf
import io.github.e1turin.circulator.ffm.model.Model
import io.github.e1turin.circulator.state.MutableStateProjectionDelegate
import io.github.e1turin.circulator.state.StateProjectionType
import kotlin.reflect.KProperty



public class ForeignMemoryStateProjectionDelegate<T>(
    private val memoryAccessor: ForeignMemoryMutator<T>,
    private val offset: Long
) : MutableStateProjectionDelegate<FfmStateful, T> {
    override fun getValue(thisRef: FfmStateful, property: KProperty<*>): T {
        return memoryAccessor.getValue(thisRef.state, offset)
    }

    override fun setValue(thisRef: FfmStateful, property: KProperty<*>, value: T) {
        memoryAccessor.setValue(thisRef.state, offset, value)
    }
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
): MutableStateProjectionDelegate<FfmStateful, T> {
    require(offset < numStateBytes) { "Offset $offset bounded to state size $numStateBytes" }

    val clazz = T::class
    val memoryAccessor = accessorOf<T>()
    val byteSize: Long = memoryAccessor.byteSize

    require(offset + byteSize in 1..numStateBytes) {
        "Accessed memory - $byteSize bytes for ${clazz.qualifiedName} (${projectionType.name}) on offset $offset -" +
            " must be in bounds of state size $numStateBytes but it is not!"
    }

    return ForeignMemoryStateProjectionDelegate(memoryAccessor, scaledOffset(offset, byteSize))
}


public inline fun <reified T> Model.input(offset: Long) = plainStateProjection<T>(offset, _root_ide_package_.io.github.e1turin.circulator.state.StateProjectionType.INPUT)
public inline fun <reified T> Model.output(offset: Long) = plainStateProjection<T>(offset, _root_ide_package_.io.github.e1turin.circulator.state.StateProjectionType.OUTPUT)
public inline fun <reified T> Model.register(offset: Long) = plainStateProjection<T>(offset, _root_ide_package_.io.github.e1turin.circulator.state.StateProjectionType.REGISTER)
//public inline fun <reified T> Model.memory(offset: Long, layout: MemoryLayoutBuilder.()->MemoryLayout) = plainStateProjection<T>(offset, _root_ide_package_.io.github.e1turin.circulator.state.StateProjectionType.MEMORY).also { MemoryLayoutBuilder.layout() }
public inline fun <reified T> Model.wire(offset: Long) = plainStateProjection<T>(offset, _root_ide_package_.io.github.e1turin.circulator.state.StateProjectionType.WIRE)

