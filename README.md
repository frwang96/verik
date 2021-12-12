# Verik

[![Test](https://github.com/frwang96/verik/actions/workflows/Sanity.yml/badge.svg)](
https://github.com/frwang96/verik)
[![Maven](https://maven-badges.herokuapp.com/maven-central/io.verik/verik-core/badge.svg?style=flat)](
https://search.maven.org/search?q=io.verik)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](
https://opensource.org/licenses/Apache-2.0)


[Verik](https://verik.io) is a hardware description language (HDL) that compiles transparently to SystemVerilog. It
borrows the syntax of Kotlin, a modern, general-purpose programming language, and reinterprets it with the semantics of
an HDL. Verik aims to improve the syntax and environment support for SystemVerilog while maintaining its core semantics
in order to bridge the gap with modern software development practices. Refer to
[examples](https://github.com/frwang96/verik-examples) for examples and
[template](https://github.com/frwang96/verik-template) for a template project.

## Dependencies

### JDK
Install the [Azul Zulu](https://www.azul.com/downloads/?version=java-17-lts&package=jdk) build of Java JDK 17.0.1.
This is an open source build of OpenJDK, an implementation of Java SE. Check the installation with the following
command.

```
% java --version
openjdk 17.0.1 2021-10-19 LTS
OpenJDK Runtime Environment Zulu17.30+15-CA (build 17.0.1+12-LTS)
OpenJDK 64-Bit Server VM Zulu17.30+15-CA (build 17.0.1+12-LTS, mixed mode, sharing)
```

### IntelliJ IDEA

Verik makes use IntelliJ IDEA to improve the coding experience over Verilog. Coding in a command line editor such as
vim or emacs is not recommended. Install [IntelliJ IDEA Community](https://www.jetbrains.com/idea/download/).

## Project Structure

The structure of a typical project is shown below. Project source files are in `src` and build outputs are in `build`.

```
.
├── build
│   ├── verik
│   └── verik-import
├── build.gradle.kts
├── settings.gradle.kts
└── src
    └── main
        └── kotlin
              └── Main.kt

```

The toolchain registers two Gradle tasks to each Verik project. These tasks can be executed through the Gradle toolbar
in IntelliJ IDEA.

- `verik`: Generates SystemVerilog output from the source files.
- `verikImport`: Imports SystemVerilog declarations to be used in the project.

## Project Configuration

The toolchain is configured in `build.gradle.kts`. The version of the plugin is set in the `plugin` block.

```kotlin
plugins {
    id("io.verik.verik-plugin") version "0.1.9"
}
```

The `verik` Gradle task is configured in the `verik` block. Default values are shown below.

```kotlin
verik {
    timescale = "1ns / 1ns"             // Simulation timescale
    debug = false                       // Prints compiler stack traces for errors and warnings
    suppressedWarnings = listOf()       // Suppressed warnings
    promotedWarnings = listOf()         // Promoted warnings
    maxErrorCount = 20                  // Maximum error count before terminating compilation
    labelSourceLocations = true         // Generate files labeling original source locations
    wrapLength = 120                    // Line wrap length
    indentLength = 4                    // Indent length
    enableDeadCodeElimination = true    // Enable dead code elimination
}
```

The `verikImport` Gradle task is configured in the `verikImport` block. Default values are shown below.

```kotlin
verikImport {
    importedFiles = listOf()            // List of SystemVerilog files to import
    debug = false                       // Prints importer stack traces for errors and warnings
    suppressedWarnings = listOf()       // Suppressed warnings
    promotedWarnings = listOf()         // Promoted warnings
    labelSourceLocations = true         // Label original source locations
}
```

## Local Build

To build the toolchain locally from source clone this repository and execute the `mainInstall` gradle task.

```
git clone https://github.com/frwang96/verik
cd verik
./gradlew mainInstall
```

Set the version of the plugin in `build.gradle.kts` to `local-SNAPSHOT`.

```kotlin
plugins {
    id("io.verik.verik-plugin") version "local-SNAPSHOT"
}
```
