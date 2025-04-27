//> using scala "2.13.12"
//> using dep "org.chipsalliance::chisel:6.7.0"
//> using plugin "org.chipsalliance:::chisel-plugin:6.7.0"
//> using options "-unchecked", "-deprecation", "-language:reflectiveCalls", "-feature", "-Xcheckinit", "-Xfatal-warnings", "-Ywarn-dead-code", "-Ywarn-unused", "-Ymacro-annotations"

import chisel3._
// _root_ disambiguates from package chisel3.util.circt if user imports chisel3.util._
import _root_.circt.stage.ChiselStage


class Dut extends Module {
  val count = IO(Output(UInt(8.W)))
  
  val counter = RegInit(0.U(8.W))
  count := counter

  counter := counter + 1.U
}

object Main extends App {
  ChiselStage.emitCHIRRTLFile(
    new Dut,
    Array("--target-dir", "gen"),
  )
}
