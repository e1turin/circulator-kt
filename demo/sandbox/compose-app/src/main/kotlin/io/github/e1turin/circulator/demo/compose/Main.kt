package io.github.e1turin.circulator.demo.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.e1turin.circulator.demo.controls.CounterDeviceController
import kotlinx.coroutines.delay
import java.lang.foreign.Arena

fun main() {
    /* Caught WrongThreadException as arena is accessed
     * from not owning thread. Quick fix is to use Shared arena.
     */
    Arena.ofShared().use { arena ->
        val controller = CounterDeviceController(arena)

        application {
            ControlPanel(controller, onCloseRequest = ::exitApplication)
        }
    }
}

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

val elementStyle = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
val buttonShape = CircleShape
val resetButtonColor = Color(0XFFE03131)

@Composable
fun CounterDeviceController.Panel() {
    var count by remember { mutableStateOf(counter.countValue) }
    var auto by remember { mutableStateOf(false) }

    LaunchedEffect(auto) {
        while (auto) {
            counter.click()
            count = counter.countValue
            delay(1000)
        }
    }

    Column {
        Box(elementStyle) {
            Text(
                count.toString(),
                modifier = elementStyle,
                style = MaterialTheme.typography.h4,
                textAlign = TextAlign.Center,
            )
        }
        Row(elementStyle, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { auto = !auto },
                shape = buttonShape,
            ) {
                Text(if (auto) "1s" else "off")
            }
            Button(
                onClick = { counter.click(); count = counter.countValue },
                modifier = Modifier.weight(1f),
                shape = buttonShape,
            ) {
                Text("click")
            }
            Button(
                onClick = { counter.reset(); count = counter.countValue },
                shape = buttonShape,
                colors = ButtonDefaults.buttonColors(backgroundColor = resetButtonColor, contentColor = Color.White),
            ) {
                Text("reset")
            }
        }
    }
}

