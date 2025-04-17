package io.github.e1turin.circulator.state

import java.lang.foreign.MemorySegment

public interface Stateful {
    public val state: MemorySegment
}
