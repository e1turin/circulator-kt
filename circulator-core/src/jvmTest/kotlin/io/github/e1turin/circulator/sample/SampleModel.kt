package io.github.e1turin.circulator.sample

import io.github.e1turin.circulator.dsl.*
import io.github.e1turin.circulator.mem.BasicFfmLibrary
import io.github.e1turin.circulator.model.BasicArcModel
import io.github.e1turin.circulator.model.FfmBasicArcModel
import io.github.e1turin.circulator.model.toArcFfmModelLib
import io.github.e1turin.circulator.state.FfmStateful
import io.github.e1turin.circulator.types.Bit
import io.github.e1turin.circulator.types.ReadOnlyMemory
import io.github.e1turin.circulator.types.bit
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment


interface DevReadOnly {
    val clk: Bit
    val rst: Bit
    val mem: ReadOnlyMemory<Int>
    val res: Byte
}

class Dev private constructor(
    override val state: MemorySegment
): DevReadOnly, FfmStateful {

    override var clk by input { signalOf<Bit>() offset 0 }

    override var rst by input { signalOf<Bit>() offset 0 }

    override val mem by memory { 4 * signalOf<Int>() offset 3 }

    override val res by output { signalOf<Byte>() offset 8 }

    companion object {
        private const val STATE_SIZE: Long = 8

        fun model(devArena: Arena, libArena: Arena = Arena.ofAuto()): BasicArcModel<Dev> = FfmBasicArcModel(
            "Dev",
            Dev(devArena.allocate(STATE_SIZE)),
            BasicFfmLibrary("dev", libArena).toArcFfmModelLib()
        )
    }
}


fun test() {
    Arena.ofAuto().use {
        val counter = Dev.model(it)

        counter.eval {
            clk = 0.bit
        }

        counter.eval {
            rst = 1.bit
            counter.eval(10) { clk = !clk }
            rst = 1.bit
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
