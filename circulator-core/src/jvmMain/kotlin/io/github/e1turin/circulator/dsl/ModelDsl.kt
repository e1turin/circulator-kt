package io.github.e1turin.circulator.dsl

import io.github.e1turin.circulator.mem.BasicFfmLibrary
import io.github.e1turin.circulator.model.BasicArcModel
import io.github.e1turin.circulator.model.FfmBasicArcModel
import io.github.e1turin.circulator.model.FfmDevFactory
import io.github.e1turin.circulator.model.toArcFfmModelLib
import io.github.e1turin.circulator.state.FfmStateful
import java.lang.foreign.Arena


public fun <D : FfmStateful> Models.arcilatorBased(
    factory: FfmDevFactory<D>,
    config: FfmArcilatorModelConfig.() -> Unit
): BasicArcModel<D> {
    val config = FfmArcilatorModelConfig().apply(config)

    require(config.dev.arena != null) { "Device arena must be specified" }

    return FfmBasicArcModel(
        config.dev.name ?: factory.devName,
        factory.build(config.dev.arena!!),
        BasicFfmLibrary(
            config.lib.name ?: factory.libName,
            config.lib.arena ?: Arena.ofAuto()
        ).toArcFfmModelLib()
    )
}


public class FfmArcilatorModelConfig {
    public val dev: DevConfig = DevConfig()
    public val lib: LibConfig = LibConfig()

    public fun dev(config: DevConfig.() -> Unit): Unit = dev.config()

    public fun lib(config: LibConfig.() -> Unit): Unit = lib.config()

    public class DevConfig {
        public var name: String? = null
        public var arena: Arena? = null
    }

    public class LibConfig {
        public var name: String? = null
        public var arena: Arena? = null
    }
}

public inline fun <D> BasicArcModel<D>.eval(
    times: Int = 1,
    block: D.() -> Unit
) = repeat(times) {
    view.block()
    eval()
}
