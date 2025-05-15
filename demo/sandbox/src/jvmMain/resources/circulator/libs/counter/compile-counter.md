## 1. Build Model

Produce LLVM IR and state file

- for model described with CIRCT MLIR `counter.mlir` (in `src/jvmMain/circt/counter`),
- for state file in arcilator JSON format `counter-states.json` (in `src/jvmMain/resources/arcilator`),
- for temporary LLVM IR output `counter.ll`,

do:

```sh
arcilator counter.mlir --emit-llvm --observe-memories --observe-named-values --observe-ports --observe-registers --observe-wires --state-file=counter-states.json -o counter.ll
```

## 2. Create Dynamic Library

Compile `counter.ll` model in LLVM IR to dynamic library (in `src/jvmMain/resources/circulator/libs/counter/`) with `clang` driver.

### Linux

On Linux JVM needs just shared library `libcounter.so`:

```sh
clang -shared -o libcounter.so counter.ll
```

And set up `LD_LIBRARY_PATH` when run the JVM (`java.library.path` is not enough!).

### Windows

On Windows JVM needs DLL `counter.dll` and compiler requires explicit export of symbols:

```sh
clang -shared -o counter.dll counter.ll -Wl,/DEF:counter.def
```

Contents of basic `counter.def`:

```def
LIBRARY counter
EXPORTS
    Counter_eval
```

Alternatively you can manually instruct linker with `/EXPORT:Counter_eval` option to export specific function.

And set up `java.library.path` variable when run the JVM.

### MacOS

On macOS JVM needs dynamic library `libcounter.dylib` and compiler needs explicit options.

```sh
clang -nostartfiles -nodefaultlibs -dynamiclib counter.ll -lSystem -o libcounter.dylib
```

CIRCT project [offers only x86_64 binaries][circt-release], but alternatively you
can [build CIRCT project from source][circt-build] and use aarch64 binaries for compilations.
Also you can use `-arch x86_64` option to force x86_64 compilation and use produced library with x86 version of JDK.

[circt-release]: https://github.com/llvm/circt/releases/
[circt-build]: https://github.com/llvm/circt/#setting-this-up
