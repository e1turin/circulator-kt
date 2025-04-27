# Architecture Notes

This library initially was targeted as [Controls.kt Framework](https://github.com/sciprogcentre/controls-kt) feature for running
device models in circuit level as it is useful for constructing and testing SCADA systems built on top of it. But a decision was made to
separate the library from the Controls.kt project and design it as some kind of bridge to native models.

## Circulator.kt pipeline

A pipeline of converting hardware device model in HDl to Kotlin code is shown in diagram below.

![circulator-pipeline.excalidraw.svg](../resources/circulator-pipeline.excalidraw.svg)
