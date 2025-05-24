package io.github.e1turin.circulator.demo.controls

import io.github.e1turin.circulator.demo.chisel.generated.CounterChiselModel
import space.kscience.controls.api.Device
import space.kscience.controls.spec.*
import space.kscience.dataforge.context.Context
import space.kscience.dataforge.context.Factory
import space.kscience.dataforge.meta.Meta
import java.lang.foreign.Arena
import kotlin.time.Duration.Companion.seconds

interface ICounterDevice : Device {
    fun reset()
    fun click()
    val countValue: Int
}

class CounterDevice(context: Context, meta: Meta, arena: Arena) :
    DeviceBySpec<ICounterDevice>(Spec, context, meta), ICounterDevice {

    val model: CounterChiselModel = CounterChiselModel.instance(arena, "counterchisel")

    override fun reset() {
        model.reset = 1.toUByte()
        model.clock = 1.toUByte()
        model.eval()
        model.clock = 0.toUByte()
        model.eval()
        model.reset = 0.toUByte()
    }

    override fun click() {
        model.clock = 1.toUByte()
        model.eval()
        model.clock = 0.toUByte()
        model.eval()
    }

    override val countValue: Int get() = model.count.toInt()

    companion object Spec : DeviceSpec<ICounterDevice>(), FfmMetaFactory<CounterDevice> {
        val count by numberProperty { countValue }
        val reset by unitAction { reset() }
        val click by unitAction { click() }

        override suspend fun ICounterDevice.onOpen() {
            doRecurring(1.seconds) {
                read(count)
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
