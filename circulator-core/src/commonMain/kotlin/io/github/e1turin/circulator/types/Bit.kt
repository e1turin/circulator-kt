package io.github.e1turin.circulator.types

import kotlin.experimental.and
import kotlin.experimental.xor

/**
 * Represents a single bit value. Utility class for more descriptive models.
 *
 * @property value The underlying byte value, where only the least significant bit is used.
 */
@JvmInline
public value class Bit @PublishedApi internal constructor(internal val value: Byte) {
    public operator fun not(): Bit = Bit(value xor 0x01)

    public fun toByte(): Byte = value and 0x01
    public fun toInt(): Int = toByte().toInt()
    public fun toBoolean(): Boolean = value != 0.toByte()
}

public inline fun Byte.toBit(): Bit = Bit(this)

public inline fun Int.toBit(): Bit = Bit(this.toByte())

public inline fun Long.toBit(): Bit = Bit(this.toByte())

public inline fun Boolean.toBit(): Bit = Bit(if (this) 1 else 0)

public val Int.bit: Bit
    get() = this.toBit().also {
        require(this == 0 || this == 1) { "Bit can contain only 0 or 1 but got $this" }
    }

public val Boolean.bit: Bit get() = this.toBit()
