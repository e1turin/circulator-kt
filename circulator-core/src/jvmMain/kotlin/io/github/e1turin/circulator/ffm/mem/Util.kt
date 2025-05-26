package io.github.e1turin.circulator.ffm.mem

import io.github.e1turin.circulator.types.Bit

public inline fun <reified T> accessorOf(): ForeignMemoryMutator<T> {
    @Suppress("UNCHECKED_CAST")
    return when (val clazz = T::class) {
        Byte::class -> ByteFMMutator
        UByte::class -> UByteFMMutator
        Int::class -> IntFMMutator
        UInt::class -> UIntFMMutator
        Long::class -> LongFMMutator
        ULong::class -> ULongFMMutator
        Boolean::class -> BooleanFMMutator
        Float::class -> FloatFMMutator
        Double::class -> DoubleFMMutator
        Char::class -> CharFMMutator
        Bit::class -> BitFMMutator
        else -> throw IllegalArgumentException("Unsupported type: $clazz")
    } as ForeignMemoryMutator<T>
}

