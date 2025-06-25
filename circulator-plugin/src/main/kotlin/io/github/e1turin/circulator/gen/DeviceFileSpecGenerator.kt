package io.github.e1turin.circulator.gen

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.github.e1turin.circulator.model.FfmDevFactory
import io.github.e1turin.circulator.state.FfmStateful
import io.github.e1turin.circulator.state.StateProjectionType
import org.jetbrains.kotlin.com.intellij.util.applyIf
import java.lang.foreign.Arena


class FileConfig(
    val packageName: String,
    val fileName: String,
    val classConfig: ClassConfig,
    val ifaceConfig: InterfaceConfig?,
    val propsConfig: List<PropertyConfig>
)

class DeviceFileGeneratorV1(
    val config: FileConfig
): FileGenerator {
    override fun fileSpec(): FileSpec {

        val builder = FileSpec.builder(config.packageName, config.fileName)
            .addFileComment(HEADER_COMMENT)
            .addInterface()
            .addClass()

        return builder.build()
    }

    private fun FileSpec.Builder.addClass() = apply {
        val gen = DeviceClassGenV1(config.classConfig, config.propsConfig)
        addType(gen.classSpec())
    }

    private fun FileSpec.Builder.addInterface() = apply {
        config.ifaceConfig?.let {
            val gen = DeviceIfaceGenV1(it, config.propsConfig)
            addType(gen.interfaceSpec())
        }
    }
}


class InterfaceConfig(
    val interfaceName: String,
)

class DeviceIfaceGenV1(
    val config: InterfaceConfig,
    val propsConfig: List<PropertyConfig>
): InterfaceGenerator {
    override fun interfaceSpec(): TypeSpec {
        val builder = TypeSpec.interfaceBuilder(config.interfaceName)
        // TODO: extra prop config?
        // TODO: add iface gen logic
        return builder.build()
    }
}


class ClassConfig(
    val className: ClassName,
    val companionConfig: CompanionConfig
)

class CompanionConfig(
    val stateSize: Long,
    val devName: String,
    val libName: String
)

class DeviceClassGenV1(
    val config: ClassConfig,
    val propsConfig: List<PropertyConfig>
): ClassGenerator, ClassCompanionGenerator {
    override fun classSpec(): TypeSpec {
        val builder = TypeSpec.classBuilder(config.className)
            .superclass(FfmStateful::class)
            .addProps()
            .addCompanion()

        return builder.build()
    }

    private fun TypeSpec.Builder.addCompanion() = apply {
        addType(companionSpec())
    }

    private fun TypeSpec.Builder.addProps() = apply {
        addProperties( // TODO: optimize
            propsConfig.map { DevicePropGenV1(it).propertySpec() }
        )
    }

    override fun companionSpec(): TypeSpec {
        val stateSizeProp = PropertySpec.builder("STATE_SIZE", LONG)
            .addModifiers(KModifier.PRIVATE, KModifier.CONST)
            .initializer("%L", config.companionConfig.stateSize)
            .build()

        val devNameProp = PropertySpec.builder("devName", String::class)
            .addModifiers(KModifier.OVERRIDE)
            .initializer("%S", config.companionConfig.devName)
            .build()

        val libNameProp = PropertySpec.builder("libName", String::class)
            .addModifiers(KModifier.OVERRIDE)
            .initializer("%S", config.companionConfig.libName)
            .build()

        val factoryMethod = FunSpec.builder("build")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("arena", Arena::class)
            .returns(config.className)
            .addStatement("return %T(arena.allocate(${stateSizeProp.name}))", config.className)
            .build()

        val builder = TypeSpec.companionObjectBuilder()
            .superclass(FfmDevFactory::class.asClassName().parameterizedBy(config.className))
            .addProperty(stateSizeProp)
            .addProperty(devNameProp)
            .addProperty(libNameProp)
            .addFunction(factoryMethod)

        return builder.build()
    }
}


class PropertyConfig(
    val name: String, // not forget 'internal' suffix
    val open: Boolean,
    val override: Boolean,
    val mutable: Boolean,
    val delegate: MemberName,
    val type: TypeName,
    val numBits: Int,
    val offset: Long,
)

// TODO: split to memory prop
class DevicePropGenV1(
    val config: PropertyConfig
): PropertyGenerator {
    private val propertyTemplate = "%M { signalOf<%T>() bits %L offset %L }"

    override fun propertySpec(): PropertySpec {
        val builder = PropertySpec.builder(config.name, config.type)
            .applyIf(config.override) { addModifiers(KModifier.OVERRIDE) }
            .applyIf(config.open) { addModifiers(KModifier.OPEN) }
            .mutable(config.mutable)
            .delegate(
                CodeBlock.of(
                    propertyTemplate,
                    config.delegate,
                    config.type,
                    config.numBits,
                    config.offset
                )
            )
        return builder.build()
    }
}

internal fun createFileConfig(
    settings: GenerationSettings
): FileConfig = with(settings) {
    val ifaceCfg = null
    val classCfg = ClassConfig(
        className = ClassName(packageName, modelInfo.name),
        companionConfig = CompanionConfig(
            stateSize = modelInfo.numStateBytes.toLong(), // cast from ULong
            devName = modelInfo.name,
            libName = modelInfo.name.lowercase()
        )
    )

    // TODO: Improve
    val allowedTypes = setOf(StateProjectionType.INPUT, StateProjectionType.OUTPUT)
    val propsCfg = modelInfo.states
        .filter { it.type in allowedTypes }
        .map {
            PropertyConfig(
                name = it.name,
                open = false,
                override = false,
                mutable = false,
                delegate = propertyDelegate(it.type),
                type = ClassName("kotlin.lang", "Long"),
                numBits = it.numBits.toInt(), // cast from UInt
                offset = it.offset.toLong() // cast from ULong
            )
        }

    val fileCfg = FileConfig(
        packageName = packageName,
        fileName = modelInfo.name,
        classConfig = classCfg,
        ifaceConfig = ifaceCfg,
        propsConfig = propsCfg
    )

    return fileCfg
}

private fun propertyDelegate(type: StateProjectionType): MemberName {
    val packageName = "io.github.e1turin.circulator.dsl"

    // TODO: add debug*, mutableMemory, ...
    val delegateName = when(type) {
        StateProjectionType.INPUT -> "input"
        StateProjectionType.OUTPUT -> "output"
        StateProjectionType.REGISTER -> "register"
        StateProjectionType.MEMORY -> "memory"
        StateProjectionType.WIRE -> "wire"
    }

    return MemberName(packageName, delegateName)
}

internal fun generateFileSpec(settings: GenerationSettings): FileSpec {
    val gen: FileGenerator = DeviceFileGeneratorV1(createFileConfig(settings))
    return gen.fileSpec()
}
