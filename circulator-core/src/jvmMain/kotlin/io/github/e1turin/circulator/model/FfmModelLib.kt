package io.github.e1turin.circulator.model

import io.github.e1turin.circulator.mem.FfmLibrary
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle


public fun interface FfmModelLib {
    public fun handle(symbolName: String): MethodHandle
}

public fun FfmLibrary.toArcilatorFfmLib(): FfmModelLib =
    FfmModelLib { sym -> find(sym, FunctionDescriptor.ofVoid(ValueLayout.ADDRESS)) }
