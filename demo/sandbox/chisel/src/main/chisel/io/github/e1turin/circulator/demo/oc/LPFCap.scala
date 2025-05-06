package io.github.e1turin.circulator.demo.oc

import chisel3._
import chisel3.util._

/** Цифровой низкочастотный фильтр (емкостной) */
class LPFCap(val filterWidth: Int) extends Module {
  require(filterWidth > 1, "filterWidth должен быть больше 1")
  val io = IO(new Bundle {
    val sclr = Input(Bool()) // синхронный сброс
    val in   = Input(Bool()) // входной сигнал
    val out  = Output(Bool()) // отфильтрованный выход
    val init = Output(Bool()) // индикатор инициализации
  })

  val midValue = (1 << (filterWidth - 1)).U
  val maxValue = ((1 << filterWidth) - 1).U

  val cnt = RegInit(midValue)
  when(io.sclr) {
    cnt := midValue
  }.elsewhen(io.in && cnt =/= maxValue) {
    cnt := cnt + 1.U
  }.elsewhen(!io.in && cnt =/= 0.U) {
    cnt := cnt - 1.U
  }

  val outReg = RegInit(false.B)
  when(io.sclr) {
    outReg := false.B
  }.elsewhen(cnt === maxValue) {
    outReg := true.B
  }.elsewhen(cnt === 0.U) {
    outReg := false.B
  }
  io.out := outReg

  val initReg = RegInit(true.B)
  when(io.sclr) {
    initReg := true.B
  }.elsewhen(cnt === maxValue || cnt === 0.U) {
    initReg := false.B
  }
  io.init := initReg
}
