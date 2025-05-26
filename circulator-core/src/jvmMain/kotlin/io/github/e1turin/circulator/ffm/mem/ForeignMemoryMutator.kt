package io.github.e1turin.circulator.ffm.mem

import io.github.e1turin.circulator.state.MemoryMutator
import io.github.e1turin.circulator.state.MemoryViewer
import java.lang.foreign.MemorySegment


public interface ForeignMemoryViewer<T>: MemoryViewer<MemorySegment, T>

public interface ForeignMemoryMutator<T>: MemoryMutator<MemorySegment, T>, ForeignMemoryViewer<T>
