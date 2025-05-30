package io.github.e1turin.circulator.dsl

import io.github.e1turin.circulator.model.BasicArcilatorModel


public inline fun <D> BasicArcilatorModel<D>.eval(times: Int = 1, block: D.() -> Unit) = repeat(times) {
    view.block()
    eval()
}
