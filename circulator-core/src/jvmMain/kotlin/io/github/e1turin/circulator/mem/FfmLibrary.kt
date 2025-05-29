package io.github.e1turin.circulator.mem

import java.lang.foreign.Arena
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.Linker
import java.lang.foreign.MemorySegment
import java.lang.foreign.SymbolLookup
import java.lang.invoke.MethodHandle


public fun interface FfmLibrary {
    public fun find(symbolName: String, descriptor: FunctionDescriptor): MethodHandle
}

public class FfmLibraryImpl(
    name: String,
    arena: Arena
) : FfmLibrary {

    override fun find(symbolName: String, descriptor: FunctionDescriptor): MethodHandle {
        val symbol: MemorySegment = symbolLookup.find(symbolName).orElseThrow {
            UnsatisfiedLinkError("unresolved symbol: '$symbolName'")
        }
        val handle = Linker.nativeLinker().downcallHandle(symbol, descriptor)
        return handle
    }

    private val symbolLookup: SymbolLookup = SymbolLookup.libraryLookup(System.mapLibraryName(name), arena)
}
