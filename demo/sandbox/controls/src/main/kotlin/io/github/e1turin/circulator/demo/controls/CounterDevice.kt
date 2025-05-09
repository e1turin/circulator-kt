package io.github.e1turin.circulator.demo.controls

import io.github.e1turin.circulator.demo.chisel.generated.CounterChiselModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import space.kscience.controls.api.Device
import space.kscience.controls.spec.DeviceBySpec
import space.kscience.controls.spec.DeviceSpec
import space.kscience.controls.spec.doRecurring
import space.kscience.controls.spec.execute
import space.kscience.controls.spec.numberProperty
import space.kscience.controls.spec.read
import space.kscience.controls.spec.unitAction
import space.kscience.dataforge.context.Context
import space.kscience.dataforge.context.Factory
import space.kscience.dataforge.meta.Meta
import java.lang.foreign.Arena
import kotlin.random.Random
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
        model.reset = 1
        for (i in 1..Random.nextInt(10)) model.eval()
        model.reset = 0
    }

    override fun click() {
        model.clock = 1
        model.eval()
        model.clock = 0
        model.eval()
    }

    override val countValue: Int get() = model.count.toInt()

    companion object Spec : DeviceSpec<ICounterDevice>(), FfmMetaFactory<CounterDevice> {
        override fun factory(arena: Arena) = Factory {
            context, meta -> CounterDevice(context, meta, arena)
        }

        val count by numberProperty { countValue }
        val reset by unitAction { reset() }
        val click by unitAction { click() }

        override suspend fun ICounterDevice.onOpen() {
            doRecurring(1.seconds) {
                read(count)
            }
        }
    }
}

interface FfmMetaFactory<T> {
    fun factory(arena: Arena): Factory<T>
}

fun CounterDevice.clickHandler() = launch {
    execute(CounterDevice.click)
}

fun CounterDevice.resetHandler() = launch {
    execute(CounterDevice.reset)
}

fun CounterDevice.getCount() = async { read(CounterDevice.count) }
