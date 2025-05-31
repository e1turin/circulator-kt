package io.github.e1turin.circulator.model

import java.lang.foreign.Arena


public interface FfmDevFactory<D> {
    public fun build(arena: Arena): D
    public val devName: String
    public val libName: String
}
