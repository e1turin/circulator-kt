# Compare FFM API

## Raw

Raw FFM API is flexible, but requires lots of boilerplate code and lots of effort to support it.

## Homebrew Wrappers

Flexible way, but it requires time to write wrappers and abstractions. 
And most important, it needs lots of effort to hold them in consistent state.

## Jextract

[Jextract][jextract] is official tool in Panama Project of OpenJDK which targets mechanical generating Java FFM API
bindings to native libraries from theirs C headers.

There are some problems:

1. horrifying generated classes with memory access via static methods,
2. it requires existing C header,
3. the header must be in consistent state (symbols may depend on names in HDL),
4. implicit dependencies in build (for used headers, seems not handled in [krakowski plugin][krakowski-plugin]).

But it's simple if you need just to call some C libs.

## Native Memory Processor

[Java Native Memory Processor][native-memory-processor] is a pet project, which offers annotation based approach for 
generating more friendly FFM bindings in contrast with Jextract way.

It also targets to handling C libraries and so requires headers and other source stuff. And it is still pet project.

[jextract]: https://github.com/openjdk/jextract
[krakowski-plugin]: https://github.com/krakowski/gradle-jextract
[native-memory-processor]: https://github.com/DigitalSmile/native-memory-processor
