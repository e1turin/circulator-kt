package io.github.e1turin.circulator.model

import io.github.e1turin.circulator.mem.FfmLibrary
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle


public fun interface FfmModelLib {
    public fun handle(symbolName: String): MethodHandle
}

/**
 * Constructor of FfmModelLib that follows Arcilator's ABI
 */
public fun FfmLibrary.toArcFfmModelLib(): FfmModelLib =
    FfmModelLib { sym -> find(sym, FunctionDescriptor.ofVoid(ValueLayout.ADDRESS)) }
