package io.github.e1turin.circulator.ffm.model

import io.github.e1turin.circulator.ffm.state.FfmStateful
import java.lang.foreign.MemorySegment

public abstract class Model(
    public val name: String,
    override val state: MemorySegment,
    public val lib: ModelLibrary,
    public val numStateBytes: Long
) : FfmStateful

