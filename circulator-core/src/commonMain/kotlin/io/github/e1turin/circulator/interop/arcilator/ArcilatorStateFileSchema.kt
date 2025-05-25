package io.github.e1turin.circulator.interop.arcilator

import io.github.e1turin.circulator.state.StateProjectionType
import kotlinx.serialization.Serializable

/*
 Classes for parsing data in Arcilator's output JSON state file https://github.com/llvm/circt/blob/main/include/circt/Dialect/Arc/ModelInfo.h
 */

/**
 * State file contains root array of models in JSON file
 */
public typealias StateFile = List<ModelInfo>

/**
 * Model attributes.
 * Permalink: https://github.com/llvm/circt/blob/218c1857b44a7d8a54d9005135bf993d8893854c/include/circt/Dialect/Arc/ModelInfo.h#L35C1-L49C3
 * ```cpp
 *  struct ModelInfo {
 *    std::string name;
 *    size_t numStateBytes;
 *    llvm::SmallVector<StateInfo> states;
 *    // ...
 * };
 * ```
 */
@Serializable
public data class ModelInfo(
    val name: String,
    val numStateBytes: ULong,
    val initialFnSym: String,
    val finalFnSym: String,
    val states: List<StateInfo>
)


/**
 * Observable state attributes
 * Permalink: https://github.com/llvm/circt/blob/218c1857b44a7d8a54d9005135bf993d8893854c/include/circt/Dialect/Arc/ModelInfo.h#L25C1-L32C3
 * ```cpp
 * struct StateInfo {
 *   enum Type { Input, Output, Register, Memory, Wire } type;
 *   std::string name;
 *   unsigned offset;
 *   unsigned numBits;
 *   unsigned memoryStride = 0; // byte separation between memory words
 *   unsigned memoryDepth = 0;  // number of words in a memory
 * };
 * ```
 *
 * State type enum extracted to independent class.
 */
@Serializable
public data class StateInfo(
    val type: StateProjectionType,
    val name: String,
    val offset: UInt,
    val numBits: UInt,
    val memoryStrides: UInt = 0u, // bytes per word
    val memoryDepth: UInt = 0u, // number of words
)
