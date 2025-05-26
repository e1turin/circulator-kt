package io.github.e1turin.circulator.state

import io.github.e1turin.circulator.mem.FfmStateMutator
import io.github.e1turin.circulator.mem.FfmStateViewer
import kotlin.reflect.KProperty


public interface FfmStateProjectionReadOnlyDelegate<T> : StateProjectionReadOnlyDelegate<FfmStateful, T>

public interface FfmStateProjectionReadWriteDelegate<T> : FfmStateProjectionReadOnlyDelegate<T>,
    StateProjectionReadWriteDelegate<FfmStateful, T>


public class FfmStateProjectionReadOnlyDelegateImpl<T>(
    private val memoryAccessor: FfmStateViewer<T>,
    private val offset: Long
) : FfmStateProjectionReadOnlyDelegate<T> {

    override fun getValue(thisRef: FfmStateful, property: KProperty<*>): T {
        return memoryAccessor.getValue(thisRef.state, offset)
    }
}

public class FfmStateProjectionReadWriteDelegateImpl<T>(
    private val memoryAccessor: FfmStateMutator<T>,
    private val offset: Long
) : FfmStateProjectionReadWriteDelegate<T> {

    override fun getValue(thisRef: FfmStateful, property: KProperty<*>): T {
        return memoryAccessor.getValue(thisRef.state, offset)
    }

    override fun setValue(thisRef: FfmStateful, property: KProperty<*>, value: T) {
        memoryAccessor.setValue(thisRef.state, offset, value)
    }
}
