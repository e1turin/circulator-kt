package io.github.e1turin.circulator.state

import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty


public interface StateProjectionDelegate<in S, out T> : ReadOnlyProperty<S, T>

public interface MutableStateProjectionDelegate<in S, T> : StateProjectionDelegate<S, T>, ReadWriteProperty<S, T>

public interface MutableStateProjectionDelegateProvider<S, T> : MutableStateProjectionDelegate<S, T>,
    PropertyDelegateProvider<S, MutableStateProjectionDelegate<S, T>>
