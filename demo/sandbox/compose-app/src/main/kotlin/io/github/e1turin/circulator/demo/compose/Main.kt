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
import io.github.e1turin.circulator.demo.controls.CounterDeviceController
import io.github.e1turin.circulator.demo.controls.clickHandler
import io.github.e1turin.circulator.demo.controls.resetHandler
import java.lang.foreign.Arena

fun main() {
    Arena.ofConfined().use { arena ->
        val controller = CounterDeviceController(arena)

        application {
            ControlPanel(controller, onCloseRequest = ::exitApplication)
        }
    }
}

val buttonStyle = Modifier.fillMaxWidth()

@Composable
fun ControlPanel(controller: CounterDeviceController, onCloseRequest: () -> Unit) {

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
@Composable
fun CounterDeviceController.Panel() {
    Column {
        /* ERROR: java.lang.WrongThreadException: Attempted access outside owning thread
             as model calls VarHandle on Segment... */
        Text(counter.countValue.toString())
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

