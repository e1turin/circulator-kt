package io.github.e1turin.circulator.gen

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import org.jetbrains.kotlin.com.intellij.util.applyIf


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
    public fun companionSpec(): TypeSpec
}

public interface PropertyGenerator {
    public fun propertySpec(): PropertySpec
}


public class FileConfig(
    public val packageName: String,
    public val fileName: String,
    public val classConfig: ClassConfig,
    public val ifaceConfig: InterfaceConfig?,
    public val propsConfig: List<PropertyConfig>
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
        val gen = DeviceClassGenV1(config.classConfig, config.propsConfig)
        addType(gen.classSpec())
    }

    private fun FileSpec.Builder.addInterface(): FileSpec.Builder = apply {
        config.ifaceConfig?.let {
            val gen = DeviceIfaceGenV1(it, config.propsConfig)
            addType(gen.interfaceSpec())
        }
    }
}


public class InterfaceConfig(
    public val interfaceName: String,
)

public class DeviceIfaceGenV1(
    public val config: InterfaceConfig,
    public val propsConfig: List<PropertyConfig>
): InterfaceGenerator {
    override fun interfaceSpec(): TypeSpec {
        val builder = TypeSpec.interfaceBuilder(config.interfaceName)
        // TODO: extra prop config?
        // TODO: add iface gen logic
        return builder.build()
    }
}


public class ClassConfig(
    public val className: String,
    public val companionConfig: CompanionConfig
)

public class DeviceClassGenV1(
    public val config: ClassConfig,
    public val propsConfig: List<PropertyConfig>
): ClassGenerator {
    override fun classSpec(): TypeSpec {
        val builder = TypeSpec.classBuilder(config.className)
            .addProps()
            .addCompanion()

        return builder.build()
    }

    private fun TypeSpec.Builder.addCompanion() = apply {
        val gen = DeviceClassCompanionGenV1(config.companionConfig)
        addType(gen.companionSpec())
    }

    private fun TypeSpec.Builder.addProps() = apply {
        propsConfig // TODO: optimize
            .map { DevicePropGenV1(it).propertySpec() }
            .forEach { addProperty(it) }
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

public class PropertyConfig(
    public val name: String,
    public val type: TypeName,
    public val open: Boolean,
    public val override: Boolean,
    public val mutable: Boolean,
)

public class DevicePropGenV1(
    public val config: PropertyConfig
): PropertyGenerator {
    override fun propertySpec(): PropertySpec {
        val builder = PropertySpec.builder(config.name, config.type)
            .applyIf(config.override) { addModifiers(KModifier.OVERRIDE) }
            .applyIf(config.open) { addModifiers(KModifier.OPEN) }
            .mutable(config.mutable)
            .addDelegate()
        //TODO: prop build logic
        return builder.build()
    }

    private fun PropertySpec.Builder.addDelegate() = apply {
        // TODO
    }
}
