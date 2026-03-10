//> using scala "2.13.12"
//> using dep "org.chipsalliance::chisel:6.7.0"
//> using plugin "org.chipsalliance:::chisel-plugin:6.7.0"
//> using options "-unchecked", "-deprecation", "-language:reflectiveCalls", "-feature", "-Xcheckinit", "-Xfatal-warnings", "-Ywarn-dead-code", "-Ywarn-unused", "-Ymacro-annotations"

package io.github.e1turin.circulator.demo.counter

import _root_.circt.stage.ChiselStage
import chisel3._


class CounterChisel extends Module {
  val count = IO(Output(UInt(8.W)))
  
  val counter = RegInit(0.U(8.W))
  count := counter

  counter := counter + 1.U
}

class ClickCounter extends Module {
  val io = IO(new Bundle {
    val click = Input(Bool())
    val count = Output(UInt(8.W))
  })

  val count = RegInit(0.U(8.W))
  io.count := count

  when(io.click) {
    count := count + 1.U
  }
}

object Main extends App {
  ChiselStage.emitCHIRRTLFile(
    new CounterChisel,
    Array("--target-dir", System.getProperty("chisel.output.dir")),
  )
  ChiselStage.emitCHIRRTLFile(
    new ClickCounter,
    Array("--target-dir", System.getProperty("chisel.output.dir")),
  )
}
