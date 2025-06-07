package io.github.e1turin.circulator.model

import io.github.e1turin.circulator.state.FfmStateful
import java.lang.invoke.MethodHandle


public class FfmBasicArcModel<D: FfmStateful>(
    name: String,
    override val io: D,
    lib: FfmModelLib,
) : BasicArcModel<D> {

    override fun eval() {
        evalHandle.invokeExact(io.state)
    }

    private val evalHandle: MethodHandle = lib.handle("${name}_eval")
}


public class FfmCompleteArcModel<D: FfmStateful>(
    name: String,
    override val io: D,
    lib: FfmModelLib,
) : CompleteArcModel<D> {

    override fun eval() {
        evalHandle.invokeExact(io.state)
    }

    override fun initial() {
        initialHandle.invokeExact(io.state)
    }

    override fun finally() {
        finallyHandle.invokeExact(io.state)
    }

    private val evalHandle: MethodHandle = lib.handle("${name}_eval")
    private val initialHandle: MethodHandle = lib.handle("${name}_initial")
    private val finallyHandle: MethodHandle = lib.handle("${name}_final")
}
