# Compare FFM API

## Raw

Raw FFM API is flexible, but requires lots of boilerplate code and lots of effort to support it.

## Homebrew Wrappers

Flexible way, but it requires time to write wrappers and abstractions. 
And most important, it needs lots of effort to hold them in consistent state.

## Jextract

With Jextract there are some problems:

1. horrifying generated API with static class methods,
2. it requires existing C header,
3. the header must be in consistent state (symbols depend on names in HDL),
4. not configured build dependencies (for headers, in krakowski plugin).

But it's simple if you need just to call some C libs.
