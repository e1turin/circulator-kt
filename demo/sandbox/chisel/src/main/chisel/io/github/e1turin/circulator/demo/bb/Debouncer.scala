package io.github.e1turin.circulator.demo.bb

import chisel3._
import chisel3.util._

class Debouncer(
  debounceCycles: Int = 1000,
  clockedEdgeOut: Boolean = false,
  inputWhenIdle: Boolean = true
) extends Module {
  val io = IO(new Bundle {
    val in = Input(Bool())
    val out = Output(Bool())
  })

  val idleValue = if (inputWhenIdle) true.B else false.B
  val oldInput = RegInit(idleValue)  // Renamed from old
  val isRunning = RegInit(false.B)  // Renamed from running
  val bounceTimeout = RegInit(0.U(log2Ceil(debounceCycles + 1).W))  // Renamed from bounce_timeout
  val outputReg = RegInit(false.B)  // Renamed from outReg
  io.out := outputReg

  when(oldInput =/= io.in) {
    isRunning := true.B
    bounceTimeout := debounceCycles.U
  }.elsewhen(isRunning) {
    bounceTimeout := bounceTimeout - 1.U
    when(bounceTimeout === 0.U) {
      isRunning := false.B
      if (clockedEdgeOut) {
        outputReg := Mux(inputWhenIdle.B, ~io.in, io.in)
      } else {
        outputReg := io.in
      }
    }.otherwise {
      if (clockedEdgeOut) {
        outputReg := false.B
      }
    }
  }.otherwise {
    if (clockedEdgeOut) {
      outputReg := false.B
    }
  }

  oldInput := io.in
}
