package io.github.e1turin.circulator.sample

import io.github.e1turin.circulator.dsl.*
import io.github.e1turin.circulator.mem.BasicFfmLibrary
import io.github.e1turin.circulator.model.CompleteArcilatorModel
import io.github.e1turin.circulator.model.FfmModelLib
import io.github.e1turin.circulator.model.toArcilatorFfmLib
import io.github.e1turin.circulator.state.FfmStateful
import io.github.e1turin.circulator.types.Bit
import io.github.e1turin.circulator.types.bit
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.invoke.MethodHandle


abstract class Dev : FfmStateful {
    var clk by input { signalOf<Bit>() offset 0 }
    var reset by input { signalOf<Bit>() offset 0 }
    val mem by memory { 4 * signalOf<Int>() offset 3 }
    val res by output { signalOf<Byte>() offset 8 }

    companion object Fabric {
        private const val STATE_SIZE: Long = 8

        fun build(arena: Arena): Dev = object : Dev() {
            override val state: MemorySegment = arena.allocate(STATE_SIZE)
        }
    }
}

class DevModel(
    override val view: Dev,
    lib: FfmModelLib
) : CompleteArcilatorModel<Dev> {

    override fun eval() {
        evalHandle.invokeExact(view.state)
    }

    override fun initial() {
        initialHandle.invokeExact(view.state)
    }

    override fun finally() {
        finallyHandle.invokeExact(view.state)
    }

    private val evalHandle: MethodHandle = lib.handle("Dev_eval")
    private val initialHandle: MethodHandle = lib.handle("Dev_initial")
    private val finallyHandle: MethodHandle = lib.handle("Dev_final")

    companion object Fabric {
        fun build(devArena: Arena, libArena: Arena = Arena.ofAuto()) = DevModel(
            Dev.build(devArena),
            BasicFfmLibrary("dev", libArena).toArcilatorFfmLib()
        )
    }
}


fun test() {
    Arena.ofAuto().use {
        val counter = DevModel.build(it, it)

        counter.eval {
            clk = 0.bit
        }

        counter.eval {
            reset = 1.bit
            counter.eval(10) { clk = !clk }
            reset = 1.bit
        }

        counter.view.mem[2] = 1

        counter.eval(20) { clk = !clk }

        counter.view.apply {
            mem[2] = res.toInt()
        }

        counter.eval(20) { clk = !clk }

        val result = counter.view.res

    }
}
