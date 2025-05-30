package io.github.e1turin.circulator.dsl

import io.github.e1turin.circulator.model.BasicArcModel


public inline fun <D> BasicArcModel<D>.eval(times: Int = 1, block: D.() -> Unit) = repeat(times) {
    view.block()
    eval()
}
