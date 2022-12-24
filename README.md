[![Maven](https://img.shields.io/github/v/release/frwang96/verik)](
https://search.maven.org/search?q=io.verik)
[![License](https://img.shields.io/github/license/frwang96/verik)](
https://opensource.org/licenses/Apache-2.0)


Verik is a hardware description language (HDL) for designing and verifying digital integrated circuits.
The goal of Verik is to enable the modern IDE workflow to bridge the technology gap between hardware and software
development.
Verik aims to be a drop-in replacement for SystemVerilog.
It leverages the productivity of the modern software stack to enable faster onboarding, shorter schedules, and reduced
engineering costs.
Verik builds upon [Kotlin](https://kotlinlang.org), a modern general-purpose programming language with a clean and
expressive syntax.
Verik is Kotlin reinterpreted with the semantics of an HDL.
It can directly make use of tools built for the vibrant Kotlin ecosystem, such as the widely used
[IntelliJ IDEA IDE](https://www.jetbrains.com/idea/).
Verik is translated to SystemVerilog by the Verik compiler.
This translation process is direct, typically with one-to-one correspondence between the input and output source files.
Verik generates readable SystemVerilog similar to what an engineer would have written.

The Verik toolchain connects the Kotlin and SystemVerilog environments.
The Verik compiler translates Verik source code into SystemVerilog.
However, we can also go in the other direction.
If we had SystemVerilog source code that we would like to reference from the Kotlin environment, the Verik importer can
be used to import the declaration headers.
This allows us to make use of SystemVerilog libraries such as the
[UVM](https://www.accellera.org/downloads/standards/uvm) directly in Verik.
It also enables us to benefit from the productivity improvements of Verik without having to rewrite any legacy code.

For examples of Verik projects refer to [examples](https://github.com/frwang96/verik-examples).
For a template project refer to [template](https://github.com/frwang96/verik-template).

Verik is an open source project under the Apache License, Version 2.0.
It was initially developed as a master's thesis project at the Massachusetts Institute of Technology.

## Local Build

Verik releases are published through [maven central](https://search.maven.org/search?q=io.verik).
To build the toolchain locally from source clone this repository and execute the `mainInstall` gradle task.

```
git clone https://github.com/frwang96/verik
cd verik
./gradlew mainInstall
```

To reference the local build in a project, set the version of the plugin in `build.gradle.kts` to `local-SNAPSHOT`.

```kotlin
plugins {
    id("io.verik.verik-plugin") version "local-SNAPSHOT"
}
```

## Gradle Tasks

Verik makes use of [gradle](https://gradle.org) for build automation.
Here are the main gradle tasks for the project.
Each module also defines its own tasks for building, testing, and reformatting.
These can be accessed from the Gradle toolbar.

- `mainCheck`: Run the tests and lint checks.
- `mainClean`: Clean all build directories.
- `mainFormat`: Reformat the source code.
- `mainGenerate`: Run the ANTLR parser generator for the importer.
- `mainInstall`: Install the gradle plugin.
- `mainTest`: Run the tests.
- `mainVerik`: Run the toolchain on the sandbox project.
- `mainVerikCompile`: Run the compiler on the sandbox project.
- `mainVerikImport`: Run the importer on the sandbox project.

## Gradle Modules

The Verik toolchain is structured as a gradle composite build.
The final output of the build is a [gradle plugin](https://plugins.gradle.org/plugin/io.verik.verik-plugin) that
packages the importer and compiler, allowing them to be run as gradle tasks in Verik projects.
The project consists of the following gradle modules.

### verik-kotlin
Documentation for declarations from the Kotlin standard library that are shared with Verik.
This is used to generate part of the API doc but are not used anywhere else in the toolchain.

### verik-core
The core library that is imported in all Verik projects that is used to express HDL semantics.
The Verik compiler and importer recognize these as built-in declarations.

### verik-importer
The importer that brings in SystemVerilog declarations to be used in Verik projects. 
The importer parses SystemVerilog source files with [ANTLR](https://www.antlr.org) and executes a sequence of stages
to convert the syntax trees to Verik.

### verik-compiler
The compiler that generates SystemVerilog output from Verik source files.
The compiler first runs the Kotlin compiler front-end and extracts its intermediate syntax tree representation.
It then executes a sequence of stages to convert the syntax trees to SystemVerilog.

### verik-plugin
The gradle plugin that packages the importer and compiler.
It configures the toolchain based on the `verik` block that is specified in `build.gradle.kts`.

### verik-sandbox
A sandbox project that can be used for quick development and debugging.
The toolchain is automatically recompiled when the sandbox project is run, so there is no need to execute `mainInstall`.
Verilog or SystemVerilog source code to be imported should be placed under `verik-sandbox/src/main/verilog` and Verik
source code to be compiled should be placed under `verik-sandbox/src/main/kotlin`.
