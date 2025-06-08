package io.github.e1turin.circulator.gen

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.github.e1turin.circulator.model.FfmDevFactory
import io.github.e1turin.circulator.state.FfmStateful
import org.jetbrains.kotlin.com.intellij.util.applyIf
import java.lang.foreign.Arena


fun interface FileGenerator {
    fun fileSpec(): FileSpec
}

fun interface InterfaceGenerator {
    fun interfaceSpec(): TypeSpec
}

fun interface ClassGenerator {
    fun classSpec(): TypeSpec
}

fun interface ClassCompanionGenerator {
    fun companionSpec(): TypeSpec
}

fun interface PropertyGenerator {
    fun propertySpec(): PropertySpec
}


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
            .addClass()
            .addInterface()

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
    override fun propertySpec(): PropertySpec {
        val builder = PropertySpec.builder(config.name, config.type)
            .applyIf(config.override) { addModifiers(KModifier.OVERRIDE) }
            .applyIf(config.open) { addModifiers(KModifier.OPEN) }
            .mutable(config.mutable)
            .delegate(
                CodeBlock.of(
                    "%M { signalOf<%T>() bits %L offset %L }",
                    config.delegate,
                    config.type,
                    config.numBits,
                    config.offset
                )
            )
        return builder.build()
    }
}
