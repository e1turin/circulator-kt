package io.github.e1turin.circulator.gen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LONG
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import io.github.e1turin.circulator.model.FfmDevFactory
import org.jetbrains.kotlin.com.intellij.util.applyIf
import java.lang.foreign.Arena


fun interface FileGenerator {
    fun fileSpec(): FileSpec
}

fun interface InterfaceGenerator {
    fun interfaceSpec (): TypeSpec
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
    val className: String,
    val companionConfig: CompanionConfig
)

class DeviceClassGenV1(
    val config: ClassConfig,
    val propsConfig: List<PropertyConfig>
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


class CompanionConfig(
    val stateSize: Long,
    val devName: String,
    val libName: String
)

class DeviceClassCompanionGenV1(
    val config: CompanionConfig
): ClassCompanionGenerator {
    override fun companionSpec(): TypeSpec {
        // TODO: does it work so? Iface is generic
        val factoryIface = ClassName("io.github.e1turin.circulator.model", "FfmDevFactory")
        val className = ClassName("", "")
        val arenaClassName = ClassName("lang.java.forign", "Arena")

        val stateSizeProp = PropertySpec.builder("STATE_SIZE", LONG)
            .addModifiers(KModifier.PRIVATE, KModifier.CONST)
            .initializer("%L", config.stateSize)
            .build()

        val builder = TypeSpec.companionObjectBuilder()
            .superclass(factoryIface.parameterizedBy(className))
            .addProperty(stateSizeProp)
            .addProperty(
                PropertySpec.builder("devName", String::class.java)
                    .addModifiers(KModifier.OVERRIDE)
                    .initializer("%S", config.devName)
                    .build()
            )
            .addProperty(
                PropertySpec.builder("libName", String::class.java)
                    .addModifiers(KModifier.OVERRIDE)
                    .initializer("%S", config.libName)
                    .build()
            )
            .addFunction(
                FunSpec.builder("build")
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter("arena", arenaClassName)
                    .returns(className)
                    .addStatement("return $className(arena.allocate(${stateSizeProp.name}))")
                    .build()
            )
        return builder.build()
    }
}

class PropertyConfig(
    val name: String, // not forget 'internal' suffix
    val type: TypeName,
    val open: Boolean,
    val override: Boolean,
    val mutable: Boolean,
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
            .addDelegate()
        //TODO: prop build logic
        return builder.build()
    }

    private fun PropertySpec.Builder.addDelegate() = apply {
        val cb = CodeBlock.builder()
        delegate(cb.build())
        // TODO
    }
}
