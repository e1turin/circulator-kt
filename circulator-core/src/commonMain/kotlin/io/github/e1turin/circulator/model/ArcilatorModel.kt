package io.github.e1turin.circulator.model


public interface BasicArcilatorModel<Dev> : WithEval {
    public val view: Dev
}

public interface CompleteArcilatorModel<Dev> : BasicArcilatorModel<Dev>, WithInitial, WithFinally


public fun interface WithEval {
    public fun eval()
}

public fun interface WithInitial {
    public fun initial()
}

public fun interface WithFinally {
    public fun finally()
}
