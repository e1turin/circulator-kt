package io.github.e1turin.circulator.demo.bb

import chisel3._
import io.github.e1turin.circulator.demo.bb.Debouncer

class RotaryEncoder(debounceCycles: Int = 0) extends Module {
  val io = IO(new Bundle {
    val inA = Input(Bool())
    val inB = Input(Bool())
    val outCcw = Output(Bool())
    val outCw = Output(Bool())
  })

  val debouncedA = Wire(Bool())
  val debouncedB = Wire(Bool())

  // Generate debounced inputs
  if (debounceCycles == 0) {
    debouncedA := io.inA
    debouncedB := io.inB
  } else {
    val inputADebouncer = Module(new Debouncer(debounceCycles))
    inputADebouncer.io.in := io.inA
    debouncedA := inputADebouncer.io.out

    val inputBDebouncer = Module(new Debouncer(debounceCycles))
    inputBDebouncer.io.in := io.inB
    debouncedB := inputBDebouncer.io.out
  }

  val marker = !(debouncedA || debouncedB)
  val previousMarker = RegNext(marker, init = false.B)  // Renamed from previous_marker

  io.outCcw := previousMarker && debouncedB
  io.outCw := previousMarker && debouncedA
}
