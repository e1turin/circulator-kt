package io.github.e1turin.circulator.ffm.model

import java.lang.foreign.*
import java.lang.invoke.MethodHandle

public abstract class ModelLibrary(
    public val name: String,
    arena: Arena,
    public val evalFnSym: String,
    public val initialFnSym: String,
    public val finalFnSym: String,
) {
    init {
//        System.loadLibrary(name)
    }

    public val libraryName: String = System.mapLibraryName(name)

    public val symbolLookup: SymbolLookup = SymbolLookup
        .libraryLookup(libraryName, arena)

    public abstract val evalFunctionHandle: MethodHandle

    public abstract val initialFunctionHandle: MethodHandle

    public abstract val finalFunctionHandle: MethodHandle

    protected fun functionHandle(symbolName: String): MethodHandle {
        val symbol: MemorySegment = symbolLookup.find(symbolName).orElseThrow {
            UnsatisfiedLinkError("unresolved symbol: '$symbolName'")
        }
        val descriptor = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS)
        val handle = Linker.nativeLinker().downcallHandle(symbol, descriptor)
        return handle
    }
}
