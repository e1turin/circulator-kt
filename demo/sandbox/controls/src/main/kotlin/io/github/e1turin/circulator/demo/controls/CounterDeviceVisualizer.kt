package io.github.e1turin.circulator.demo.controls

import io.ktor.server.engine.EmbeddedServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.html.div
import kotlinx.html.link
import space.kscience.controls.api.PropertyChangedMessage
import space.kscience.controls.client.magixFormat
import space.kscience.controls.manager.DeviceManager
import space.kscience.controls.spec.name
import space.kscience.dataforge.meta.number
import space.kscience.magix.api.MagixEndpoint
import space.kscience.magix.api.subscribe
import space.kscience.plotly.Plotly
import space.kscience.plotly.layout
import space.kscience.plotly.models.Trace
import space.kscience.plotly.plot
import space.kscience.plotly.trace
import space.kscience.visionforge.plotly.serveSinglePage
import java.util.concurrent.ConcurrentLinkedQueue

fun <T> Flow<T>.windowed(size: Int): Flow<Iterable<T>> {
    val queue = ConcurrentLinkedQueue<T>()
    return flow {
        this@windowed.collect {
            queue.add(it)
            if (queue.size >= size) {
                queue.poll()
            }
            emit(queue)
        }
    }
}

suspend fun Trace.updateFrom(axisName: String, flow: Flow<Iterable<Number>>) {
    flow.collect {
        axis(axisName).numbers = it
    }
}

fun CoroutineScope.startCounterDeviceServer(magixEndpoint: MagixEndpoint): EmbeddedServer<*, *> {
    //share subscription to a parse message only once
    val subscription = magixEndpoint.subscribe(DeviceManager.magixFormat).shareIn(this, SharingStarted.Lazily)

    val input_clockFlow = subscription.mapNotNull { (_, payload) ->
        (payload as? PropertyChangedMessage)?.takeIf { it.property == CounterDevice.input_clock.name }
    }.map { it.value }

    val output_io_countFlow = subscription.mapNotNull { (_, payload) ->
        (payload as? PropertyChangedMessage)?.takeIf { it.property == CounterDevice.output_io_count.name }
    }.map { it.value }

    val input_resetFlow = subscription.mapNotNull { (_, payload) ->
        (payload as? PropertyChangedMessage)?.takeIf { it.property == CounterDevice.input_reset.name }
    }.map { it.value }

    val wire_io_clickFlow = subscription.mapNotNull { (_, payload) ->
        (payload as? PropertyChangedMessage)?.takeIf { it.property == CounterDevice.wire_io_click.name }
    }.map { it.value }

    return Plotly.serveSinglePage(port = 9091, routeConfiguration = {
        updateInterval = 100
    }) {
        link {
            rel = "stylesheet"
            href = "https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css"
            attributes["integrity"] = "sha384-9aIt2nRpC12Uk9gS9baDl411NQApFmC26EwAOH8WgZl5MYYxFfc+NcPb1dKGj7Sk"
            attributes["crossorigin"] = "anonymous"
        }
        div("row") {
            div("col-6") {
                plot {
                    layout {
                        title = "output_io_count"
                        xaxis.title = "point index"
                        yaxis.title = "count"
                    }
                    trace {
                        launch {
                            val flow: Flow<Iterable<Number>> = output_io_countFlow
                                .mapNotNull { it.number }
                                .windowed(100)
                            updateFrom(Trace.Y_AXIS, flow)
                        }
                    }
                }
            }
            div("col-6") {
                plot {
                    layout {
                        title = "input_clock"
                        xaxis.title = "point index"
                        yaxis.title = "clock"
                    }
                    trace {
                        launch {
                            val flow: Flow<Iterable<Number>> = input_clockFlow
                                .mapNotNull { it.number }
                                .windowed(100)
                            updateFrom(Trace.Y_AXIS, flow)
                        }
                    }
                }
            }
        }
        div("row") {
            div("col-6") {
                plot {
                    layout {
                        title = "input_reset"
                        xaxis.title = "point index"
                        yaxis.title = "reset"
                    }
                    trace {
                        launch {
                            val flow: Flow<Iterable<Number>> = input_resetFlow
                                .mapNotNull { it.number }
                                .windowed(100)
                            updateFrom(Trace.Y_AXIS, flow)
                        }
                    }
                }
            }
            div("col-6") {
                plot {
                    layout {
                        title = "wire_io_click"
                        xaxis.title = "point index"
                        yaxis.title = "внутренний сигнал click"
                    }
                    trace {
                        launch {
                            val flow: Flow<Iterable<Number>> = wire_io_clickFlow
                                .mapNotNull { it.number }
                                .windowed(100)
                            updateFrom(Trace.Y_AXIS, flow)
                        }
                    }
                }
            }
        }
    }

}

