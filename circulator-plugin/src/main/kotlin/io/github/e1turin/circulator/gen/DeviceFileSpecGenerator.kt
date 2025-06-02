package io.github.e1turin.circulator.gen

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec


public interface FileGenerator {
    public fun fileSpec(): FileSpec
}

public interface InterfaceGenerator {
    public fun interfaceSpec (): TypeSpec
}

public interface ClassGenerator {
    public fun classSpec(): TypeSpec
}

public interface ClassCompanionGenerator {
    public fun companionSpec (): TypeSpec
}


public class FileConfig(
    public val packageName: String,
    public val fileName: String,
    public val classConfig: ClassConfig,
    public val ifaceConfig: InterfaceConfig?
)

public class DeviceFileGeneratorV1(
    public val config: FileConfig
): FileGenerator {
    override fun fileSpec(): FileSpec {

        val builder = FileSpec.builder(config.packageName, config.fileName)
            .addClass()
            .addInterface()

        return builder.build()
    }

    private fun FileSpec.Builder.addClass(): FileSpec.Builder = apply {
        val gen = DeviceClassGenV1(config.classConfig)
        addType(gen.classSpec())
    }

    private fun FileSpec.Builder.addInterface(): FileSpec.Builder = apply {
        config.ifaceConfig?.let {
            val gen = DeviceIfaceGenV1(it)
            addType(gen.interfaceSpec())
        }
    }
}


public class InterfaceConfig(
    public val interfaceName: String
)

public class DeviceIfaceGenV1(
    public val config: InterfaceConfig
): InterfaceGenerator {
    override fun interfaceSpec(): TypeSpec {
        val builder = TypeSpec.interfaceBuilder(config.interfaceName)
        // TODO: add iface gen logic
        return builder.build()
    }
}


public class ClassConfig(
    public val className: String,
    public val companionConfig: CompanionConfig
)

public class DeviceClassGenV1(
    public val config: ClassConfig
): ClassGenerator {
    override fun classSpec(): TypeSpec {
        val builder = TypeSpec.classBuilder(config.className)
            .addCompanion()

        return builder.build()
    }

    private fun TypeSpec.Builder.addCompanion(): TypeSpec.Builder = apply {
        val gen = DeviceClassCompanionGenV1(config.companionConfig)
        addType(gen.companionSpec())
    }
}


public class CompanionConfig()

public class DeviceClassCompanionGenV1(
    public val config: CompanionConfig
): ClassCompanionGenerator {
    override fun companionSpec(): TypeSpec {
        val builder = TypeSpec.companionObjectBuilder()
        //TODO: companion build logic
        return builder.build()
    }
}
