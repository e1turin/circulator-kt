package io.github.e1turin.circulator.ffm.state

import io.github.e1turin.circulator.state.Stateful
import java.lang.foreign.MemorySegment

public interface FfmStateful: Stateful<MemorySegment>
