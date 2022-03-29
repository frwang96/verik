[![Verik](https://verik.io/img/logo-banner.svg)](https://verik.io)

[![Test](https://github.com/frwang96/verik/actions/workflows/Sanity.yml/badge.svg)](
https://github.com/frwang96/verik)
[![Maven](https://img.shields.io/maven-central/v/io.verik/verik-core)](
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
existing SystemVerilog infrastructure and back-end tool flows.

Refer to [examples](https://github.com/frwang96/verik-examples) for examples and
[template](https://github.com/frwang96/verik-template) for a template project.
Setup instructions, tutorials, and other information can be found in the [docs](https://verik.io/docs/overview).

## Local Build

Verik releases are published through [maven central](https://search.maven.org/search?q=io.verik).
To build the toolchain locally from source clone this repository and execute the `mainInstall` gradle task.

```
git clone https://github.com/frwang96/verik
cd verik
./gradlew mainInstall
```

To use the local build in a project set the version of the plugin in `build.gradle.kts` to `local-SNAPSHOT`.

```kotlin
plugins {
    id("io.verik.verik-plugin") version "local-SNAPSHOT"
}
```

## Contributor Guide

Anyone is welcome to contribute to Verik by raising [issues](https://github.com/frwang96/verik/issues), posting in
[discussions](https://github.com/frwang96/verik/discussions), or creating
[pull requests](https://github.com/frwang96/verik/pulls).
A helpful guide for creating pull requests can be found
[here](https://www.freecodecamp.org/news/how-to-make-your-first-pull-request-on-github-3/).
To work on the Verik toolchain, set up the [IntelliJ IDEA IDE](https://www.jetbrains.com/idea/).
In order for a pull request to be merged onto the `master` branch, it must pass all the test suites and lint checks.
Run these checks by executing the `mainCheck` gradle task.

### Directory Structure

The Verik toolchain is structured as a Gradle composite build with the following modules:

- `verik-kotlin`: Documentation for declarations from the Kotlin standard library that are shared with Verik.
- `verik-core`: Core library that is imported in all Verik projects.
- `verik-importer`: Importer that brings in SystemVerilog sources to be used in Verik projects.
- `verik-compiler`: Compiler that generates SystemVerilog output from Verik sources.
- `verik-plugin`: Gradle plugin that packages the importer and compiler.
- `verik-sandbox`: Sandbox project for development and debugging.

### Gradle Tasks

- `mainCheck`: Run the test suites and lint checks.
- `mainClean`: Clean all build directories.
- `mainFormat`: Reformat the source code.
- `mainGenerate`: Run the ANTLR parser generator for the importer.
- `mainInstall`: Install the gradle plugin.
- `mainTest`: Run the test suites.
- `mainVerik`: Run the compiler on the sandbox project.
- `mainVerikImport`: Run the importer on the sandbox project.

### Sandbox Project

The directory `verik-sandbox` contains a sandbox project that can be used for quick debugging and validation.
The toolchain is automatically recompiled when the sandbox project is run.
There is no need to run the `mainInstall` gradle task.
Verilog or SystemVerilog source code to be imported should be placed under `verik-sandbox/src/main/verilog` and Verik
source code to be compiled should be placed under `verik-sandbox/src/main/kotlin`.
The gradle tasks `mainVerikImport` and `mainVerik` runs the importer and compiler respectively.
The sandbox project is configured with `debug` enabled.
This prints the stack traces of all error and warning messages.
