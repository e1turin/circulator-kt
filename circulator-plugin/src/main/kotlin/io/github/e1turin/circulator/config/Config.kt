package io.github.e1turin.circulator.config

import io.github.e1turin.circulator.state.StateProjectionType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.File


public typealias ConfigFile = PluginConfig

@Serializable
public data class PluginConfig(
    val models: Map<ModelId, ModelConfig> = emptyMap(),
    // val baseDirPath: String
    val variables: Map<String, String> = emptyMap(),
)

public typealias ModelId = String

@OptIn(ExperimentalSerializationApi::class)
@Serializable
public data class ModelConfig(
    @SerialName("package")
    val packageName: String = "io.github.e1turin.circulator.generated",

    @SerialName("state-file")
    @Serializable(with = FileSerializer::class)
    val stateFile: File,

    @SerialName("output")
    val outputDirPath: String? = null,

    @SerialName("model")
    val modelOptions: ModelOptions = ModelOptions(),

    @SerialName("library")
    val libraryOptions: LibraryOptions = LibraryOptions(),
)

@Serializable
public data class ModelOptions(
    public val open: Boolean = false,

    @SerialName("all-states-open")
    public val allStatesOpen: Boolean = false,

    @SerialName("all-states-mutable")
    public val allStatesMutable: Boolean = false,

    @SerialName("all-states-type")
    public val allStatesType: List<StateType> = listOf(StateType.INPUT, StateType.OUTPUT),

    @SerialName("states")
    public val states: Map<StateName, StateAttributes> = emptyMap(),
)

public typealias StateName = String
public typealias StateType = StateProjectionType

@Serializable
public data class StateAttributes(
    public val open: Boolean = false,
    public val mutable: Boolean = false,
    public val access: Boolean = false,
)

@Serializable
public data class LibraryOptions(
    public val open: Boolean = false,
)


