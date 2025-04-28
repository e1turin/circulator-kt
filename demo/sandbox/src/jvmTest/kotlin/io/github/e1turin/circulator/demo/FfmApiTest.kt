package io.github.e1turin.circulator.demo

import kotlin.test.Test
import kotlin.test.assertTrue

class FfmApiTest {
    @Test
    fun `FFM API comparison`() {
        val circulator = circulatorFfmWrapper()
        val my = myFfmWrapper()
        val jextract = jextractFfm()
        val raw = rawFfm()

        assertTrue(circulator == my && my == jextract && jextract == raw)
    }
}
