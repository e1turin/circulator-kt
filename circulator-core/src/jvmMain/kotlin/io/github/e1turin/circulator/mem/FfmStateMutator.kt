package io.github.e1turin.circulator.mem

import io.github.e1turin.circulator.state.StateMutator
import io.github.e1turin.circulator.state.StateViewer
import java.lang.foreign.MemorySegment


public interface FfmStateViewer<T>: StateViewer<MemorySegment, T>

public interface FfmStateMutator<T>: FfmStateViewer<T>, StateMutator<MemorySegment, T>
