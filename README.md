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
A good starting point for understanding the high-level design of the Verik toolchain is the
[language whitepaper](https://verik.io/pdf/whitepaper.pdf).

The setup for working on the Verik toolchain is very similar to the setup for working on a Verik project.
Install JDK 17 and set up IntelliJ IDEA as specified in [setup](https://verik.io/docs/setup).
In order for a pull request to be merged onto the `master` branch it must pass all of the test suites and lint checks.
Run these checks by executing the `mainCheck` gradle task.

### Gradle Modules

Verik makes use of [gradle](https://gradle.org) for build automation.
The Verik toolchain is structured as a gradle composite build.
The final output of the build is a [gradle plugin](https://plugins.gradle.org/plugin/io.verik.verik-plugin) that
packages the importer and compiler, allowing them to be run as gradle tasks in Verik projects.
The project consists of the following gradle modules.

#### `verik-kotlin`
Documentation for declarations from the Kotlin standard library that are shared with Verik.
This is used to generate part of the [API docs](https://verik.io/api/-verik/io.verik.kotlin/index.html) but are not used
anywhere else in the toolchain.

#### `verik-core`
Core library that is imported in all Verik projects that are used to express HDL semantics.
The Verik importer and compiler recognize these as built-in declarations.

#### `verik-importer`
Importer that brings in SystemVerilog declarations to be used in Verik projects. 
The importer parses SystemVerilog source files with [ANTLR](https://www.antlr.org) and executes a sequence of stages
to convert the syntax trees to Verik source files.

#### `verik-compiler`
Compiler that generates SystemVerilog output from Verik source files.
The compiler first runs the Kotlin compiler front-end and extracts its intermediate syntax tree representation.
It then executes a sequence of stages to convert the syntax trees to SystemVerilog source files.

#### `verik-plugin`
Gradle plugin that packages the importer and compiler.
It configures the toolchain based on the `verik` and `verikImport` blocks that are specified in `build.gradle.kts`.

#### `verik-sandbox`
Sandbox project that can be used for quick development and debugging.
The toolchain is automatically recompiled when the sandbox project is run.
Verilog or SystemVerilog source code to be imported should be placed under `verik-sandbox/src/main/verilog` and Verik
source code to be compiled should be placed under `verik-sandbox/src/main/kotlin`.

### Gradle Tasks

- `mainCheck`: Run the test suites and lint checks.
- `mainClean`: Clean all build directories.
- `mainFormat`: Reformat the source code.
- `mainGenerate`: Run the ANTLR parser generator for the importer.
- `mainInstall`: Install the gradle plugin.
- `mainTest`: Run the test suites.
- `mainVerik`: Run the compiler on the sandbox project.
- `mainVerikImport`: Run the importer on the sandbox project.
