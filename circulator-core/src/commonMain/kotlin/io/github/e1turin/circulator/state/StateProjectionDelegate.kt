package io.github.e1turin.circulator.state

import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty


public interface StateProjectionReadOnlyDelegate<in S, out T> : ReadOnlyProperty<S, T>

public interface StateProjectionReadWriteDelegate<in S, T> : StateProjectionReadOnlyDelegate<S, T>, ReadWriteProperty<S, T>

public interface StateProjectionReadOnlyDelegateProvider<in S, T> : StateProjectionReadOnlyDelegate<S, T>,
    PropertyDelegateProvider<S, StateProjectionReadWriteDelegate<S, T>>

public interface StateProjectionReadWriteDelegateProvider<in S, T> : StateProjectionReadWriteDelegate<S, T>,
    PropertyDelegateProvider<S, StateProjectionReadWriteDelegate<S, T>>

