# Demo projects to illustrate Circulator usage

## Gradle build note

Project is in subdirectory but it `includeBuild("..")` circulator-kt project, which seems convenient for development: 
- load demo project configuration, and it naturally depends on building library project; 
- and alternatively if you do not need to edit demos, just load root library build configuration.

Demo project use `circulator-kt` library's `libs.versions.toml` for consistency.

## Demos

Description of shown demo projects.

### Sandbox

Project with "smoke tests" examples of Circulator features usage.

#### Counter sample
```sh
./gradlew :sandbox:runJvmCounter
```
