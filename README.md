[![Verik](https://verik.io/img/logo-banner.svg)](https://verik.io)

[![Test](https://github.com/frwang96/verik/actions/workflows/Sanity.yml/badge.svg)](
https://github.com/frwang96/verik)
[![Maven](https://maven-badges.herokuapp.com/maven-central/io.verik/verik-core/badge.svg?style=flat)](
https://search.maven.org/search?q=io.verik)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](
https://opensource.org/licenses/Apache-2.0)


[Verik](https://verik.io) is a hardware description language for designing and simulating digital integrated circuits.
The goal of Verik is to be a drop-in replacement of for SystemVerilog that leverages the productivity gains of a modern
programming language to reduce development time and cut down on engineering costs associated with design and
verification.
Verik builds upon [Kotlin](https://kotlinlang.org), a modern general-purpose programming language with a clean and
expressive syntax.
Verik is Kotlin reinterpreted with the semantics of an HDL.
It can directly make use of tools built for the vibrant Kotlin ecosystem, such as the widely used
[IntelliJ IDEA IDE](https://www.jetbrains.com/idea/).
Verik is translated to SystemVerilog by the Verik compiler.
It supports both the synthesizable and non-synthesizable aspects of SystemVerilog and is designed to integrate well with
existing infrastructure and back-end tool flows.

Refer to [examples](https://github.com/frwang96/verik-examples) for examples and
[template](https://github.com/frwang96/verik-template) for a template project.
Setup instructions and tutorials can be found in the [docs](https://verik.io/docs/overview).

The Verik toolchain is structured as a Gradle composite build with the following modules:

- `verik-compiler`: Compiler that generates SystemVerilog output from Verik sources.
- `verik-core`: Core library for Verik projects.
- `verik-importer`: Importer that brings in SystemVerilog sources to be used in Verik projects.
- `verik-kotlin`: Documentation of declarations from the Kotlin standard library that are supported by Verik.
- `verik-plugin`: Gradle plugin that packages the compiler and importer.
- `verik-sandbox`: Sandbox project for development and debugging.

## Gradle Tasks

- `mainCheck`: Run all tests and lint checks.
- `mainClean`: Clean all build directories.
- `mainFormat`: Reformat source code.
- `mainGenerate`: Run ANTLR parser generator.
- `mainInstall`: Install plugin.
- `mainTest`: Run all tests.
- `mainVerik`: Run verik task in sandbox.
- `mainVerikImport`: Run verikImport task in sandbox.

## Local Build

To build the toolchain locally from source clone this repository and execute the `mainInstall` gradle task.

```
git clone https://github.com/frwang96/verik
cd verik
./gradlew mainInstall
```

To use the local build set the version of the plugin in `build.gradle.kts` to `local-SNAPSHOT`.

```kotlin
plugins {
    id("io.verik.verik-plugin") version "local-SNAPSHOT"
}
```
