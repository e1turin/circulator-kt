package io.github.e1turin.circulator.mem

import io.github.e1turin.circulator.types.Bit

public inline fun <reified T> mutatorOf(): FfmStateMutator<T> {
    @Suppress("UNCHECKED_CAST")
    return when (val clazz = T::class) {
        Byte::class -> ByteFfmMutator
        UByte::class -> UByteFfmMutator
        Int::class -> IntFfmMutator
        UInt::class -> UIntFfmMutator
        Long::class -> LongFfmMutator
        ULong::class -> ULongFfmMutator
        Boolean::class -> BooleanFfmMutator
        Float::class -> FloatFfmMutator
        Double::class -> DoubleFfmMutator
        Char::class -> CharFfmMutator
        Bit::class -> BitFfmMutator
        else -> throw IllegalArgumentException("Unsupported type: $clazz")
    } as FfmStateMutator<T>
}

public inline fun <reified T> viewerOf(): FfmStateViewer<T> = mutatorOf<T>()

