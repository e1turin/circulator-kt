package io.github.e1turin.circulator.demo.controls

import space.kscience.controls.manager.DeviceManager
import space.kscience.controls.manager.install
import space.kscience.dataforge.context.Context
import space.kscience.dataforge.context.ContextAware
import space.kscience.dataforge.context.request
import space.kscience.dataforge.meta.Meta
import java.lang.foreign.Arena

class CounterDeviceController(arena: Arena): ContextAware {
    override val context = Context(name = "Demo") {
        plugin(DeviceManager)
    }
    val deviceManager = context.request(DeviceManager)
    val counter = deviceManager.install("counter", CounterDevice.factory(arena), Meta {})
}
