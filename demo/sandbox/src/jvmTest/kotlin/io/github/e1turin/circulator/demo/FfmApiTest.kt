package io.github.e1turin.circulator.demo

import kotlin.test.Test
import kotlin.test.assertTrue

class FfmApiTest {
    @Test
    fun `basic counter behaviour`() {

        val n = 10

        val circulator = runPrecompiledCounterModel(n)
        val my = runCustomCounterModel(n)
        val jextract = runJextractFfmApiForCounter(n)
        val raw = runRawFfmApiForCounter(n)
        val chisel = runCounterChisel(n)
//        val verilog = runCounterVerilog(n)

        assertTrue { circulator == n }
        assertTrue { my == n }
        assertTrue { jextract == n }
        assertTrue { raw == n }
        assertTrue { chisel == n }

//        assertFalse { verilog == n } // broken `circt-verilog` compiler
    }
}
