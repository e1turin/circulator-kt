package io.github.e1turin.circulator.demo

import io.github.e1turin.circulator.demo.chisel.generated.CounterChiselModel
import io.github.e1turin.circulator.demo.sandbox.generated.CounterModel
//import io.github.e1turin.circulator.demo.verilog.generated.CounterVerilogModel
import io.github.krakowski.jextract.jextracted.State
import io.github.krakowski.jextract.jextracted.counter_h
import java.lang.foreign.*


fun main() {
    playWithFFM()
}

const val libName = "counter"
// 'counter.dll' on Windows, 'libcounter.so' on linux, 'libcounter.dylib' on macOS
val properLibName = System.mapLibraryName(libName)

fun playWithFFM() {
    println("Hello JVM World!\n")

    println("proper library name: $properLibName")
    println("library search path: ${System.getProperty("java.library.path")}\n")

    System.loadLibrary(libName) // required for Windows

    val n = 10

    println("Hello Raw FFM World!")
    println("counter.o=${runRawFfmApiForCounter(n)} for $n ticks")
    println()
    println("Hello Jextract FFM World!")
    println("counter.o=${runJextractFfmApiForCounter(n)} for $n ticks")
    println()
    println("Hello Circulator World!")
    println("counter.o=${runCustomCounterModel(n)} for $n ticks")
    println()
    println("Hello Circulator (precompiled) World!")
    println("counter.o=${runPrecompiledCounterModel(n)} for $n ticks")
    println()
    println("Hello Circulator (Chisel) World!")
    println("counter.count=${runCounterChisel(n)} for $n ticks")
//    println()
//    println("Hello Circulator (Verilog) World!")
//    println("counter.count=${runCounterVerilog()} for $n ticks")
}

fun runRawFfmApiForCounter(n: Int = 10): Int {
    val lookup = SymbolLookup.libraryLookup(properLibName, Arena.ofAuto())
        .or(SymbolLookup.loaderLookup())
        .or(Linker.nativeLinker().defaultLookup())

    val linker = Linker.nativeLinker()

    val dutEval = linker.downcallHandle(
        lookup.find("Counter_eval").get(),
        FunctionDescriptor.ofVoid(ValueLayout.ADDRESS)
    )

    val stateLayout = MemoryLayout.sequenceLayout(8, ValueLayout.JAVA_BYTE)
    Arena.ofConfined().use {
        val state = it.allocate(8)

        state[ValueLayout.JAVA_BYTE, 1] = 1 // reset <= 1
        for (i in 1..7) {
            state[ValueLayout.JAVA_BYTE, 0] = 1 // clk <= 1
            dutEval.invokeExact(state)
            state[ValueLayout.JAVA_BYTE, 0] = 0 // clk <= 0
            dutEval.invokeExact(state)
        }
        state[ValueLayout.JAVA_BYTE, 1] = 0 // reset <= 0

        for (i in 1..n) {
            state[ValueLayout.JAVA_BYTE, 0] = 1 // clk <= 1
            dutEval.invokeExact(state)
            state[ValueLayout.JAVA_BYTE, 0] = 0 // clk <= 0
            dutEval.invokeExact(state)
        }

        return state[ValueLayout.JAVA_BYTE, 7].toInt()
    }
}

fun runJextractFfmApiForCounter(n: Int = 10): Int {
    Arena.ofConfined().use {
        val state = it.allocate(State.layout())

        State.reset(state, 1)
        for (i in 1..10) {
            State.clk(state, 1)
            counter_h.Counter_eval(state)
            State.clk(state, 0)
            counter_h.Counter_eval(state)
        }
        State.reset(state, 0)

        for (i in 1..n) {
            State.clk(state, 1)
            counter_h.Counter_eval(state)
            State.clk(state, 0)
            counter_h.Counter_eval(state)
        }
        return state[ValueLayout.JAVA_BYTE, 7].toInt()
    }
}

fun runCustomCounterModel(n: Int = 10): Int {
    fun <T> Arena.build(factory: Factory<T>): T {
        return factory.build(this)
    }

    Arena.ofConfined().use { arena ->
        val dut = arena.build(Dut)

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
        dut.step(n)

        return dut.o.toInt()
    }
}

fun runPrecompiledCounterModel(n: Int = 10): Int {
    Arena.ofConfined().use { arena ->
        val dut = CounterModel.instance(arena, "counter")

        fun CounterModel.step(times: Int = 1) {
            for (i in 1..times) {
                clk = 1.toUByte()
                eval()
                clk = 0.toUByte()
                eval()
            }
        }

        fun CounterModel.reset(steps: Int = 0) {
            reset = 1.toUByte()
            eval()
            step(steps)
            reset = 0.toUByte()
        }

        dut.reset(7)
        dut.step(n)

        return dut.o.toInt()
    }
}


fun runCounterChisel(n: Int = 10): Int {
    fun CounterChiselModel.tick() {
        clock = 1.toUByte()
        eval()
        clock = 0.toUByte()
        eval()
    }

    fun CounterChiselModel.init() {
        reset = 1.toUByte()
        for (i in 1..7) tick()
        reset = 0.toUByte()
    }

    Arena.ofConfined().use { arena ->
        val counter = CounterChiselModel.instance(arena, "counterchisel")

        counter.init()

        for (i in 1..n) counter.tick()

        return counter.count.toInt()
    }
}

//fun runCounterVerilog(n: Int = 10): Int {
//    fun CounterVerilogModel.tick() {
//        clk = 1
//        eval()
//        clk = 0
//        eval()
//    }
//
//    fun CounterVerilogModel.init() {
//        reset = 1
//        for (i in 1..7) tick()
//        reset = 0
//    }
//
//    Arena.ofConfined().use { arena ->
//        val counter = CounterVerilogModel.instance(arena, "counterverilog")
//
//        counter.init()
//
//        for (i in 1..n) counter.tick()
//
//        return counter.count.toInt()
//    }
//}
