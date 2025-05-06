package io.github.e1turin.circulator.demo.oc

import chisel3._
import chisel3.util._

/** Квадратурный энкодер с поддержкой ошибок и направления */
class QRotaryEnc extends Module {
  val io = IO(new Bundle {
    val sclr = Input(Bool()) // синхронный сброс
    val ena = Input(Bool()) // разрешение счета
    val dir = Input(Bool()) // направление счета
    val a = Input(Bool()) // сигнал A
    val b = Input(Bool()) // сигнал B
    val bidirCounter = Output(SInt(32.W)) // счетчик положения
    val error = Output(Bool()) // ошибка
  })

  val greyInc2 = VecInit(1.U(2.W), 3.U(2.W), 0.U(2.W), 2.U(2.W))
  val greyDec2 = VecInit(2.U(2.W), 0.U(2.W), 3.U(2.W), 1.U(2.W))

  val oldCode = RegInit(0.U(2.W))
  val curCode = Mux(io.dir, Cat(io.a, io.b), Cat(io.b, io.a))

  val cntEna = oldCode =/= curCode
  val inc = cntEna && (curCode === greyInc2(oldCode))
  val dec = cntEna && (curCode === greyDec2(oldCode))
  val err = cntEna && !(inc || dec)

  when(cntEna) {
    oldCode := curCode
  }

  val counter = RegInit(0.S(32.W))
  when(io.sclr) {
    counter := 0.S
  }.elsewhen(io.ena) {
    when(inc) {
      counter := counter + 1.S
    }.elsewhen(dec) {
      counter := counter - 1.S
    }
  }
  io.bidirCounter := counter

  val errorReg = RegInit(false.B)
  when(io.sclr) {
    errorReg := false.B
  }.elsewhen(io.ena && err) {
    errorReg := true.B
  }
  io.error := errorReg
}
