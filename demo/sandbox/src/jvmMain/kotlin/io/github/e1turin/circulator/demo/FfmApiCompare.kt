package io.github.e1turin.circulator.demo

import io.github.e1turin.circulator.demo.generated.DutModel
import io.github.krakowski.jextract.jextracted.dut_h
import io.github.krakowski.jextract.jextracted.State
import java.lang.foreign.Arena
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.Linker
import java.lang.foreign.MemoryLayout
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout


fun main() {
    playWithFFM()
}

val libName = "counter"
val properLibName = System.mapLibraryName(libName) // 'counter.dll' on Windows or 'libcounter.so' on linux

fun playWithFFM() {
    println("Hello JVM World!\n")

    println("proper library name: $properLibName")
    println("library search path: ${System.getProperty("java.library.path")}\n")

    System.loadLibrary(libName) // required for Windows

    println("Hello Raw FFM World!")
    println("dut.o=${rawFfm()}\n")

    println("Hello My FFM World!")
    println("dut.o=${myFfmWrapper()}\n")

    println("Hello Circulator FFM World!")
    println("dut.o=${circulatorFfmWrapper()}\n")

    println("Hello Jextract FFM World!")
    println("dut.o=${jextractFfm()}")
}

fun circulatorFfmWrapper(): Int {
    Arena.ofConfined().use { arena ->
        val dut = DutModel.instance(arena, libName)

        fun DutModel.step(times: Int = 1) {
            for (i in 1..times) {
                clk = 1
                eval()
                clk = 0
                eval()
            }
        }

        fun DutModel.reset(steps: Int = 0) {
            reset = 1
            eval()
            step(steps)
            reset = 0
        }

        dut.reset(10)
        dut.step(10)

        return dut.o.toInt()
    }
}

fun myFfmWrapper(): Int {
    Arena.ofConfined().use { arena ->
        val dut = Dut.instance(arena, libName)

        fun Dut.step(times: Int = 1) {
            for (i in 1..times) {
                clk = 1
                eval()
                clk = 0
                eval()
            }
        }

        fun Dut.reset(steps: Int = 0) {
            reset = 1
            eval()
            step(steps)
            reset = 0
        }

        dut.reset(10)
        dut.step(10)

        return dut.o.toInt()
    }
}

fun rawFfm(): Int {
    val lookup = SymbolLookup.libraryLookup(properLibName, Arena.ofAuto())
        .or(SymbolLookup.loaderLookup())
        .or(Linker.nativeLinker().defaultLookup())

    val linker = Linker.nativeLinker()

    val dutEval = linker.downcallHandle(
        lookup.find("Dut_eval").get(),
        FunctionDescriptor.ofVoid(ValueLayout.ADDRESS)
    )

    val stateLayout = MemoryLayout.sequenceLayout(8, ValueLayout.JAVA_BYTE)
    Arena.ofConfined().use {
        val state = it.allocate(8)

        // reset <= 1
        state[ValueLayout.JAVA_BYTE, 1] = 1
        for (i in 1..10) {
            // clk <= 1
            state[ValueLayout.JAVA_BYTE, 0] = 1
            dutEval.invokeExact(state)
            // clk <= 0
            state[ValueLayout.JAVA_BYTE, 0] = 0
            dutEval.invokeExact(state)
        }

        // reset <= 0
        state[ValueLayout.JAVA_BYTE, 1] = 0
        for (i in 1..10) {
            // clk <= 1
            state[ValueLayout.JAVA_BYTE, 0] = 1
            dutEval.invokeExact(state)
            // clk <= 0
            state[ValueLayout.JAVA_BYTE, 0] = 0
            dutEval.invokeExact(state)
        }
        return state[ValueLayout.JAVA_BYTE, 7].toInt()
    }
}

fun jextractFfm(): Int {
    Arena.ofConfined().use {
        val state = it.allocate(State.layout())

        State.reset(state, 1)
        for (i in 1..10) {
            State.clk(state, 1)
            dut_h.Dut_eval(state)
            State.clk(state, 0)
            dut_h.Dut_eval(state)
        }

        State.reset(state, 0)
        for (i in 1..10) {
            State.clk(state, 1)
            dut_h.Dut_eval(state)
            State.clk(state, 0)
            dut_h.Dut_eval(state)
        }
        return state[ValueLayout.JAVA_BYTE, 7].toInt()
    }
}
