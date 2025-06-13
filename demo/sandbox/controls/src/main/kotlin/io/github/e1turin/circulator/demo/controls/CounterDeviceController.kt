package io.github.e1turin.circulator.demo.controls

import io.ktor.server.engine.EmbeddedServer
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import org.eclipse.milo.opcua.sdk.server.OpcUaServer
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText
import space.kscience.controls.api.GetDescriptionMessage
import space.kscience.controls.api.PropertyChangedMessage
import space.kscience.controls.client.launchMagixService
import space.kscience.controls.client.magixFormat
import space.kscience.controls.manager.DeviceManager
import space.kscience.controls.manager.install
import space.kscience.controls.opcua.server.OpcUaServer
import space.kscience.controls.opcua.server.endpoint
import space.kscience.controls.opcua.server.serveDevices
import space.kscience.dataforge.context.Context
import space.kscience.dataforge.context.ContextAware
import space.kscience.dataforge.context.info
import space.kscience.dataforge.context.logger
import space.kscience.dataforge.context.request
import space.kscience.magix.api.MagixEndpoint
import space.kscience.magix.api.MagixMessage
import space.kscience.magix.api.send
import space.kscience.magix.api.subscribe
import space.kscience.magix.rsocket.rSocketWithTcp
import space.kscience.magix.rsocket.rSocketWithWebSockets
import space.kscience.magix.server.RSocketMagixFlowPlugin
import space.kscience.magix.server.startMagixServer
import java.lang.foreign.Arena

private val json = Json { prettyPrint = true }

class CounterDeviceController(): ContextAware {

    override val context = Context(name = "Counter") {
        plugin(DeviceManager)
    }
    var counter: CounterDevice? = null
    var magixServer: EmbeddedServer<*, *>? = null
    var visualizer: EmbeddedServer<*, *>? = null
    val opcUaServer: OpcUaServer = OpcUaServer {
        setApplicationName(LocalizedText.english("space.kscience.controls.opcua"))

        endpoint {
            setBindPort(4840)
            //use default endpoint
        }
    }

    private val deviceManager = context.request(DeviceManager)

    fun start(arena: Arena) = context.launch {
        counter = deviceManager.install("counter", CounterDevice.factory(arena))

        magixServer = startMagixServer(RSocketMagixFlowPlugin())/*TCP rsocket support*/

        val deviceEndpoint = MagixEndpoint.rSocketWithTcp("localhost")
        deviceManager.launchMagixService(deviceEndpoint, "counterDevice")

        val visualEndpoint = MagixEndpoint.rSocketWithWebSockets("localhost")
        visualizer = startCounterDeviceServer(visualEndpoint)

        opcUaServer.startup()
        opcUaServer.serveDevices(deviceManager)

        val listenerEndpoint = MagixEndpoint.rSocketWithWebSockets("localhost")

        // subscribe remote endpoint
        listenerEndpoint.subscribe(DeviceManager.magixFormat).onEach { (magixMessage, deviceMessage) ->
            // print all messages that are not property change message
            if (deviceMessage !is PropertyChangedMessage) {
                println(">> ${json.encodeToString(MagixMessage.serializer(), magixMessage)}")
            }
        }.launchIn(this)

        // send description request
        listenerEndpoint.send(
            format = DeviceManager.magixFormat,
            payload = GetDescriptionMessage(Clock.System.now()),
            source = "listener",
//            target = "demoDevice"
        )
    }

    fun shutdown() = context.launch {
        logger.info { "Shutting down..." }
        opcUaServer.shutdown()
        logger.info { "OpcUa server stopped" }
        visualizer?.stop(1000, 5000)
        logger.info { "Visualization server stopped" }
        magixServer?.stop(1000, 5000)
        logger.info { "Magix server stopped" }
        counter?.stop()
        logger.info { "Device server stopped" }
    }
}
