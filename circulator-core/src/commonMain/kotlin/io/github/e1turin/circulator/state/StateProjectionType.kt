package io.github.e1turin.circulator.state

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Types of state corresponding to observable objects' types.
 * Serial names are taken from json encoder in Arcilator's sources.
 * Permalink: https://github.com/llvm/circt/blob/d675c243c04339563517de1717dacbe3aa8309d5/lib/Dialect/Arc/ModelInfo.cpp#L130C3-L171C6
 */
@Serializable
public enum class StateProjectionType {

    @SerialName("input")
    INPUT,

    @SerialName("output")
    OUTPUT,

    @SerialName("register")
    REGISTER,

    @SerialName("memory")
    MEMORY,

    @SerialName("wire")
    WIRE
}
