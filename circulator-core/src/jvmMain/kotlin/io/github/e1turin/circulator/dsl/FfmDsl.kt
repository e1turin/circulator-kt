package io.github.e1turin.circulator.dsl

import io.github.e1turin.circulator.mem.FfmStateMutator
import io.github.e1turin.circulator.mem.FfmStateViewer
import io.github.e1turin.circulator.state.FfmStateProjectionReadOnlyDelegateImpl
import io.github.e1turin.circulator.state.FfmStateProjectionReadWriteDelegateImpl
import io.github.e1turin.circulator.state.FfmStateful
import io.github.e1turin.circulator.state.StateProjectionReadOnlyDelegate
import io.github.e1turin.circulator.state.StateProjectionReadWriteDelegate
import java.lang.foreign.Arena
import kotlin.use


public inline infix fun <reified T> FfmStateViewer<T>.at(offset: Long): StateProjectionReadOnlyDelegate<FfmStateful, T> {
    return FfmStateProjectionReadOnlyDelegateImpl(this, offset)
}

public inline infix fun <reified T> FfmStateMutator<T>.at(offset: Long): StateProjectionReadWriteDelegate<FfmStateful, T> {
    return FfmStateProjectionReadWriteDelegateImpl(this, offset)
}

public fun using(arena: Arena, block: Arena.() -> Unit): Unit = arena.use { it.block() }
