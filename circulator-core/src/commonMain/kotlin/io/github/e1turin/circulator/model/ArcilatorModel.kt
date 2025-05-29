package io.github.e1turin.circulator.model


public interface BasicArcilatorModel<D>: WithEval, WithUpdate<D>
public interface FullArcilatorModel<D> : WithEval, WithUpdate<D>, WithInitial, WithFinally

public fun interface WithEval {
    public fun eval()
}

public fun interface WithUpdate<D> {
    public fun update(block: D.() -> Unit)
    public operator fun invoke(block: D.() -> Unit): Unit = update(block)
}

public fun interface WithInitial {
    public fun initial()
}

public fun interface WithFinally {
    public fun finally()
}
