package io.github.e1turin.circulator.demo.console

import io.github.e1turin.circulator.demo.controls.CounterDevice
import io.github.e1turin.circulator.demo.controls.CounterDeviceController
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import space.kscience.controls.spec.execute
import space.kscience.controls.spec.read
import java.lang.foreign.Arena

fun main() {
    runBlocking {
        /* Caught WrongThreadException as arena is accessed
         * from not owning thread. Quick fix is to use Shared arena.
         */
        Arena.ofShared().use { arena ->
            val controller = CounterDeviceController(arena)

            println("actions: c[lick], r[eset], e[xit]")
            while (true) {
                print(">>> ")
                val action: String = readLine() ?: "default"
                when (action) {
                    "c", "click" -> controller.counter.execute(CounterDevice.click)
                    "r", "reset" -> controller.counter.execute(CounterDevice.reset)
                    "e", "exit" -> break
                    else -> {
                        println("Unknown action: '$action'")
                    }
                }
                val count = controller.counter.read(CounterDevice.count)
                println("Count=$count")
            }
        }
    }
}
