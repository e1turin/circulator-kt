package io.github.e1turin.circulator.demo.controls

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import space.kscience.controls.manager.DeviceManager
import space.kscience.controls.manager.install
import space.kscience.dataforge.context.Context
import space.kscience.dataforge.context.request
import space.kscience.dataforge.meta.Meta
import java.lang.foreign.Arena
import kotlin.use

fun main() {
    Arena.ofConfined().use { arena ->
        val controller = Controller(arena)
        app(controller)
    }
}

class Controller(arena: Arena) {
    val context = Context(name = "Demo") {
        plugin(DeviceManager)
    }
    val deviceManager = context.request(DeviceManager)
    val counter = deviceManager.install("counter", CounterDevice.factory(arena), Meta {})
}

val buttonStyle = Modifier//.fillMaxWidth()

fun app(controller: Controller) = application {

    val controller = remember { controller }

    Window(
        title = "Counter clicker",
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(width = 400.dp, height = 320.dp)
    ) {
        MaterialTheme {
            // TODO: do properly value update
            Text(controller.counter.countValue.toString())
            Button(
                onClick = { controller.counter.clickHandler() },
                modifier = buttonStyle
            ) {
                Text("Click")
            }
            Button(
                onClick = { controller.counter.resetHandler() },
                modifier = buttonStyle
            ) {
                Text("Reset")
            }
        }
    }
}
