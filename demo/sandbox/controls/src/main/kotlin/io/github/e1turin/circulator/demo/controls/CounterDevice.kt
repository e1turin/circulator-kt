package io.github.e1turin.circulator.demo.controls

import io.github.e1turin.circulator.demo.chisel.generated.ClickCounterModel
import kotlinx.coroutines.delay
import space.kscience.controls.api.Device
import space.kscience.controls.spec.*
import space.kscience.dataforge.context.Context
import space.kscience.dataforge.context.Factory
import space.kscience.dataforge.meta.Meta
import space.kscience.dataforge.meta.MetaConverter
import java.lang.foreign.Arena
import kotlin.time.Duration.Companion.milliseconds

interface ICounterDevice : Device {
    fun eval()
    var input_clock: Int
    var input_reset: Int
    var input_io_click: Int
    val output_io_count: Int

    val wire_clock: Byte
    val wire_reset: Byte
    val wire_io_click: Byte
    val register_count: Byte
}

class CounterDevice(context: Context, meta: Meta, arena: Arena) :
    DeviceBySpec<ICounterDevice>(Spec, context, meta), ICounterDevice {

    val model = ClickCounterModel.instance(arena, "ClickCounter")

    override fun eval() {
        model.eval()
    }

    override var input_clock: Int
        get() = model.clock.toInt()
        set(value) { model.clock = value.toByte() }
    override var input_reset: Int
        get() = model.reset.toInt()
        set(value) { model.reset = value.toByte() }
    override var input_io_click: Int
        get() = model.io_click.toInt()
        set(value) { model.io_click = value.toByte() }
    override val output_io_count: Int get() = model.io_count.toInt()

    override val wire_clock: Byte get() = model.wire_clock
    override val wire_reset: Byte get() = model.wire_reset
    override val wire_io_click: Byte get() = model.wire_io_click
    override val register_count: Byte get() = model.register_count

    companion object Spec : DeviceSpec<ICounterDevice>(), FfmMetaFactory<CounterDevice> {
        const val dumpTimeMillis = 50L
        const val tickHalfTimeMillis = 100L
        const val resetTimeMillis = 500L
        const val clickTimeMillis = 300L

        val tick by unitAction {
            write(Spec.input_clock, 1)
            delay(tickHalfTimeMillis)
            write(Spec.input_clock, 0)
        }

        val reset by unitAction {
            println("reset")
            write(Spec.input_reset, 1)
            delay(resetTimeMillis)
            write(Spec.input_reset, 0)
        }

        val click by unitAction {
            println("click")
            write(Spec.input_io_click,1)
            delay(clickTimeMillis)
            write(Spec.input_io_click, 0)
        }

        val input_clock by mutableProperty(MetaConverter.int, ICounterDevice::input_clock)//Property { this.input_clock }
        val input_reset by mutableProperty(MetaConverter.int, ICounterDevice::input_reset)
        val input_io_click by mutableProperty(MetaConverter.int, ICounterDevice::input_io_click)
        val output_io_count by numberProperty { this.output_io_count }

        val wire_clock by numberProperty { this.wire_clock }
        val wire_reset by numberProperty { this.wire_reset }
        val wire_io_click by numberProperty { this.wire_io_click }
        val register_count by numberProperty { this.register_count }

        suspend fun ICounterDevice.dumpValues() {
            read(Spec.input_clock)
            read(Spec.input_reset)
            read(Spec.input_io_click)
            read(Spec.output_io_count)
            read(Spec.wire_clock)
            read(Spec.wire_reset)
            read(Spec.wire_io_click)
            read(Spec.register_count)
        }

        override suspend fun ICounterDevice.onOpen() {
            doRecurring(dumpTimeMillis.milliseconds) {
                eval()
                dumpValues()
            }
            doRecurring((tickHalfTimeMillis * 2).milliseconds) {
                execute(tick)
            }
        }

        override fun factory(arena: Arena) = Factory { context, meta ->
            CounterDevice(context, meta, arena)
        }

    }
}

interface FfmMetaFactory<T> {
    fun factory(arena: Arena): Factory<T>
}
