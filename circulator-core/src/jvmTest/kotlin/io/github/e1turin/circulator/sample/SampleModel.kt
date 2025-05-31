package io.github.e1turin.circulator.sample

import io.github.e1turin.circulator.dsl.*
import io.github.e1turin.circulator.model.FfmDevFactory
import io.github.e1turin.circulator.state.FfmStateful
import io.github.e1turin.circulator.types.Bit
import io.github.e1turin.circulator.types.Memory
import io.github.e1turin.circulator.types.bit
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment


// make it optional
interface DevReadOnly {
    val clk: Bit
    val rst: Bit
    val mem: Memory<Int>
    val romem: Memory<Int>
    val res: Byte
}

class Dev private constructor(
    override val state: MemorySegment
): DevReadOnly, FfmStateful {

    override var clk by input { signalOf<Bit>() offset 0 }

    override var rst by input { signalOf<Bit>() offset 1 }

    override val mem by mutableMemory { 4 * signalOf<Int>() offset 3 }
    override val romem by memory { 4 * signalOf<Int>() offset 3 }

    override val res by output { signalOf<Byte>() bits 8..0 offset 8 }

    companion object Factory: FfmDevFactory<Dev> {
        private const val STATE_SIZE: Long = 8

        override val devName: String = "Dev"
        override val libName: String = "dev"

        override fun build(arena: Arena) = Dev(arena.allocate(STATE_SIZE))
    }
}


fun test() {
    Arena.ofAuto().use {
        val counter = Models.arcilatorBased(Dev) { dev.arena = it }

        counter.eval {
            clk = 0.bit
        }

        counter.eval {
            rst = 1.bit
            counter.eval(10) { clk = !clk }
            rst = 1.bit
        }

        counter.view.mem[2] = 1
        counter.view.romem[2]

        counter.eval(20) { clk = !clk }

        counter.view.apply {
            mem[2] = res.toInt()
        }

        counter.eval(20) { clk = !clk }

        val result = counter.view.res

    }
}
