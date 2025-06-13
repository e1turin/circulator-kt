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
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.e1turin.circulator.demo.controls.CounterDevice.Spec
import io.github.e1turin.circulator.demo.controls.CounterDeviceController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import space.kscience.controls.spec.execute
import java.lang.foreign.Arena

fun main() {
    /* Caught WrongThreadException as arena is accessed
     * from not owning thread. Quick fix is to use Shared arena.
     */
    Arena.ofShared().use { arena ->
        val controller = CounterDeviceController()

        controller.start(arena)
        application {
            ControlPanel(controller, onCloseRequest = {
                controller.shutdown()
                exitApplication()
            })
        }
    }
}

@Composable
fun ControlPanel(controller: CounterDeviceController, onCloseRequest: () -> Unit) {

    val controller = remember { controller }

    Window(
        title = "Counter clicker",
        onCloseRequest = onCloseRequest,
        state = rememberWindowState(width = 200.dp, height = 300.dp, position = WindowPosition(40.dp, 500.dp))
    ) {
        MaterialTheme {
            controller.Panel()
        }
    }
}

val elementStyle = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
val buttonShape = CircleShape
val resetButtonColor = Color(0XFFE03131)

/* Model of real screen which receives data as array of chars */
@Composable
fun Screen(output: Int) {
    Text(
        output.toString(),
        modifier = elementStyle,
        style = MaterialTheme.typography.h4,
        textAlign = TextAlign.Center,
    )
}

@Composable
fun CounterDeviceController.Panel() {
//    var count by remember { mutableStateOf(255) }
    var auto by remember { mutableStateOf(false) }

    LaunchedEffect(auto) {
        while (auto) {
            counter?.run {
                launch {
                    execute(Spec.click)
                }
            }
            delay(1000)
        }
    }

    Column {
//        Box(elementStyle) {
//            /* additional requirement of conversion to UByte found by e2e-testing */
//            Screen(count/*.toByte().toInt()*/)
//        }
//        Row(elementStyle, horizontalArrangement = Arrangement.spacedBy(8.dp))
        Column(elementStyle, verticalArrangement = Arrangement.spacedBy(8.dp))
        {
            Button(
                modifier = Modifier.weight(1F).fillMaxWidth(),
                onClick = { auto = !auto },
                shape = buttonShape,
            ) {
                Text(if (auto) "auto" else "off")
            }
            Button(
                modifier = Modifier.weight(1F).fillMaxWidth(),
                onClick = {
                    counter?.run {
                        launch {
                            execute(Spec.click)
                        }
                    }
                },
                shape = buttonShape,
            ) {
                Text("click")
            }
            Button(
                modifier = Modifier.weight(1F).fillMaxWidth(),
                onClick = {
                    counter?.run {
                        launch {
                            execute(Spec.reset)
                        }
                    }
                },
                shape = buttonShape,
                colors = ButtonDefaults.buttonColors(backgroundColor = resetButtonColor, contentColor = Color.White),
            ) {
                Text("reset")
            }
        }
    }
}

