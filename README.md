# Circulator.kt: Kotlin Library for Running Circuit Models. 

Circulator goal is to automate generation of models in Kotlin from HDL in another languages, e.g. [Chisel][chisel], Verilog,
etc., with [LLVM CIRCT][llvm-circt] toolchain. It also provides useful tools for modelling circuits with Kotlin.

Library offers Kotlin wrappers for native models produced by Arcilator ([video][arcilator-video],
[slides][arcilator-slides]), but in future it is supposed to support another toolchains such as [Verilator][verilator].

Library relies on JVM [FFM API][ffm-api] which imposes restriction on minimal JDK version 21. In future library
supposed to support older version of JDK via JNI layer and Kotlin/Native interop.

For development notes see [docs/notes](./docs/notes).

[chisel]: https://www.chisel-lang.org/
[llvm-circt]: https://circt.llvm.org/
[arcilator-video]: https://youtu.be/iwJBlRUz6Vw
[verilator]: https://www.veripool.org/verilator/
[arcilator-slides]: https://llvm.org/devmtg/2023-10/slides/techtalks/Erhart-Arcilator-FastAndCycleAccurateHardwareSimulationInCIRCT.pdf
[ffm-api]: https://docs.oracle.com/en/java/javase/24/core/foreign-function-and-memory-api.html
