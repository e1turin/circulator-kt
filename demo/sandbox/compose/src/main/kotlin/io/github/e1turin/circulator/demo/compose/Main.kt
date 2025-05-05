package io.github.e1turin.circulator.demo.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.e1turin.circulator.demo.controls.CounterDevice
import io.github.e1turin.circulator.demo.controls.clickHandler
import io.github.e1turin.circulator.demo.controls.resetHandler
import space.kscience.controls.manager.DeviceManager
import space.kscience.controls.manager.install
import space.kscience.dataforge.context.Context
import space.kscience.dataforge.context.request
import space.kscience.dataforge.meta.Meta
import java.lang.foreign.Arena

fun main() {
    Arena.ofConfined().use { arena ->
        val controller = Controller(arena)

        application {
            ControlPanel(controller, onCloseRequest = ::exitApplication)
        }
    }
}

class Controller(arena: Arena) {
    val context = Context(name = "Demo") {
        plugin(DeviceManager)
    }
    val deviceManager = context.request(DeviceManager)
    val counter = deviceManager.install("counter", CounterDevice.factory(arena), Meta {})

    @Composable
    fun Panel() {
        Column {
            /* ERROR: java.lang.WrongThreadException: Attempted access outside owning thread
                 as model calls VarHandle on Segment... */
            // Text(counter.countValue.toString())
            Button(
                onClick = { counter.clickHandler() },
                modifier = buttonStyle
            ) {
                Text("Click")
            }
            Button(
                onClick = { counter.resetHandler() },
                modifier = buttonStyle
            ) {
                Text("Reset")
            }
        }
    }
}

val buttonStyle = Modifier.fillMaxWidth()

@Composable
fun ControlPanel(controller: Controller, onCloseRequest: () -> Unit) {

    val controller = remember { controller }

    Window(
        title = "Counter clicker",
        onCloseRequest = onCloseRequest,
        state = rememberWindowState(width = 400.dp, height = 320.dp)
    ) {
        MaterialTheme {
            controller.Panel()
        }
    }
}
