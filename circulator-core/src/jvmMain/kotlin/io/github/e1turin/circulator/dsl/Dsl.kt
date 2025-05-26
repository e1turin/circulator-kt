package io.github.e1turin.circulator.dsl

import io.github.e1turin.circulator.ffm.mem.ForeignMemoryMutator
import io.github.e1turin.circulator.ffm.mem.accessorOf
import io.github.e1turin.circulator.ffm.state.FfmStateful
import io.github.e1turin.circulator.ffm.state.ForeignMemoryStateProjectionDelegate
import io.github.e1turin.circulator.state.MutableStateProjectionDelegate
import io.github.e1turin.circulator.state.StateProjectionDelegate
import io.github.e1turin.circulator.state.StateProjectionType


public inline fun <reified T> signalOf(type: StateProjectionType? = null): ForeignMemoryReadOnlyAccessor<T> =
    ForeignMemoryReadOnlyAccessor(accessorOf<T>())

public inline fun <reified T> mutableSignalOf(type: StateProjectionType? = null): ForeignMemoryReadWriteAccessor<T> =
    ForeignMemoryReadWriteAccessor(accessorOf<T>())

public class ForeignMemoryReadOnlyAccessor<T>(private val accessor: ForeignMemoryMutator<T>) {
    public infix fun at(offset: Long): StateProjectionDelegate<FfmStateful, T> {
        return ForeignMemoryStateProjectionDelegate(accessor, offset)
    }
}

public class ForeignMemoryReadWriteAccessor<T>(private val accessor: ForeignMemoryMutator<T>) {
    public infix fun at(offset: Long): MutableStateProjectionDelegate<FfmStateful, T> {
        return ForeignMemoryStateProjectionDelegate(accessor, offset)
    }
}
