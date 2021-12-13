# Verik

[![Test](https://github.com/frwang96/verik/actions/workflows/Sanity.yml/badge.svg)](
https://github.com/frwang96/verik)
[![Maven](https://maven-badges.herokuapp.com/maven-central/io.verik/verik-core/badge.svg?style=flat)](
https://search.maven.org/search?q=io.verik)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](
https://opensource.org/licenses/Apache-2.0)


[Verik](https://verik.io) is a hardware description language that compiles transparently to SystemVerilog. It
borrows the syntax of Kotlin, a modern, general-purpose programming language, and reinterprets it with the semantics of
an HDL. The goal of Verik is to be a drop-in replacement for SystemVerilog that enables engineers to benefit from the
productivity gains of a modern programming language without giving up the close-to-the-metal control that SystemVerilog
enables. Refer to [examples](https://github.com/frwang96/verik-examples) for examples and
[template](https://github.com/frwang96/verik-template) for a template project. Setup instructions and tutorials can
be found in the [docs](https://verik.io/docs/overview).

This project is structured as a Gradle composite build with the following modules:

- `verik-compiler`: Compiler that generates SystemVerilog output from Verik sources.
- `verik-core`: Core library for Verik projects.
- `verik-importer`: Importer that brings in SystemVerilog sources to be used in Verik projects.
- `verik-kotlin`: Documentation of declarations from the Kotlin standard library that are supported by Verik.
- `verik-plugin`: Gradle plugin that packages the compiler and importer.
- `verik-sandbox`: Sandbox project for development and debugging.

## Local Build

To build the toolchain locally from source clone this repository and execute the `mainInstall` gradle task.

```
git clone https://github.com/frwang96/verik
cd verik
./gradlew mainInstall
```

In the Verik project set the version of the plugin in `build.gradle.kts` to `local-SNAPSHOT`.

```kotlin
plugins {
    id("io.verik.verik-plugin") version "local-SNAPSHOT"
}
```
