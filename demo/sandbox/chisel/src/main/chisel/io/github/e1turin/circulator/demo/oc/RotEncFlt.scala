package io.github.e1turin.circulator.demo.oc

import chisel3._
import chisel3.util._

/** Квадратурный энкодер с фильтрацией входных сигналов */
class RotEncFlt(val filterWidth: Int = 7) extends Module {
  val io = IO(new Bundle {
    val sclr         = Input(Bool())
    val dir          = Input(Bool())
    val a            = Input(Bool())
    val b            = Input(Bool())
    val bidirCounter = Output(SInt(32.W))
    val error        = Output(Bool())
    val ready        = Output(Bool())
  })

  val aReg    = RegNext(io.a, false.B)
  val bReg    = RegNext(io.b, false.B)
  val dirReg  = RegInit(0.U(2.W))
  dirReg := Cat(dirReg(0), io.dir)
  val dirChanged = dirReg(1) ^ dirReg(0)

  val f0 = Module(new LPFCap(filterWidth))
  f0.io.sclr := dirChanged
  f0.io.in   := aReg
  val aFlt   = f0.io.out
  val init0  = f0.io.init

  val f1 = Module(new LPFCap(filterWidth))
  f1.io.sclr := dirChanged
  f1.io.in   := bReg
  val bFlt   = f1.io.out
  val init1  = f1.io.init

  val init = Cat(init1, init0)
  val readyReg = RegInit(false.B)
  when(dirChanged) {
    readyReg := false.B
  }.otherwise {
    readyReg := (init === 0.U)
  }
  io.ready := readyReg

  val enc = Module(new QRotaryEnc)
  enc.io.sclr := io.sclr || dirChanged
  enc.io.ena  := (init === 0.U)
  enc.io.dir  := dirReg(0)
  enc.io.a    := aFlt
  enc.io.b    := bFlt

  io.bidirCounter := enc.io.bidirCounter
  io.error        := enc.io.error
}
