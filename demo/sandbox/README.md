# Demo of using library for running model of simple counter

Sample output for `gradlew :sandbox:runJvmCounter`:

```console
Hello JVM World!

proper library name: libcounter.dylib
library search path: /Users/user/Projects/circulator-kt/demo/sandbox/src/jvmMain/resources/circulator/libs/counter/:/Library/Java/Extensions:/Network/Library/Java/Extensions:/System/Library/Java/Extensions:/usr/lib/java:.

Hello Raw FFM World!
counter.o=10 for 10 ticks

Hello Jextract FFM World!
counter.o=10 for 10 ticks

Hello Circulator World!
counter.o=10 for 10 ticks

Hello Circulator (precompiled) World!
counter.o=10 for 10 ticks

Hello Circulator (Chisel) World!
counter.count=10 for 10 ticks

Hello Circulator (Verilog) World!
counter.count=0 for 10 ticks
```

Verilog model is broken because of `circt-verilog` utility wrong compilation (it is not stable).
