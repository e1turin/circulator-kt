# Architecture Notes

This library initially was targeted as [Controls.kt Framework](https://github.com/sciprogcentre/controls-kt) feature for running
device models in circuit level as it is useful for constructing and testing SCADA systems built on top of it. But a decision was made to
separate the library from the Controls.kt project and design it as some kind of bridge to native models.

## Circulator.kt pipeline

A pipeline of converting hardware device model in HDl to Kotlin code is shown in diagram below.

![circulator-pipeline.excalidraw.svg](../resources/circulator-pipeline.excalidraw.svg)

## Naming Convention

**Device** is a hardware module that is described in HDL and is simulation object.

**Device State** is a full context which is used for device model evaluation.

**Device State projection** is the elementary state of a device component: wire, register, port etc., it is what called signal
in other HDL languages. Initially it refers to _state_ object in state file produced by Arcilator.

- Perhaps it should be called "signal" in a manner of existing tools, but it is not a signal in HDL sense. It could be
  synthetic class property which aggregates other _signals_. I'll think about it when the library supports more than
  just the Arcilator simulator.

**Library** is a binary object file used as a native implementation of models.
