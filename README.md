[![Verik](https://verik.io/img/logo-banner.svg)](https://verik.io)

[![Test](https://img.shields.io/github/workflow/status/frwang96/verik/Sanity)](
https://github.com/frwang96/verik)
[![Maven](https://img.shields.io/github/v/release/frwang96/verik)](
https://search.maven.org/search?q=io.verik)
[![License](https://img.shields.io/github/license/frwang96/verik)](
https://opensource.org/licenses/Apache-2.0)


[Verik](https://verik.io) is a hardware description language (HDL) for designing and verifying digital integrated
circuits.
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

![Block diagram](https://verik.io/img/overview/block-diagram.png)

The Verik toolchain connects the Kotlin and SystemVerilog environments.
Going towards the right, the Verik compiler translates Verik source code into SystemVerilog.
However, we can also go in the other direction.
If we had SystemVerilog source code that we would like to reference from the Kotlin environment, the Verik importer can
be used to import the declaration headers.
This allows us to make use of SystemVerilog libraries such as the
[UVM](https://www.accellera.org/downloads/standards/uvm) directly in Verik.
It also enables us to benefit from the productivity improvements of Verik without having to rewrite any legacy code.

To see examples of Verik projects refer to [examples](https://github.com/frwang96/verik-examples).
For a template project refer to [template](https://github.com/frwang96/verik-template).
Setup instructions, tutorials, and other information can be found in the [docs](https://verik.io/docs/overview).

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

## Contributor Guide

Anyone is welcome to contribute to Verik by raising [issues](https://github.com/frwang96/verik/issues), posting in
[discussions](https://github.com/frwang96/verik/discussions), or creating
[pull requests](https://github.com/frwang96/verik/pulls).
A helpful guide for creating pull requests can be found
[here](https://www.freecodecamp.org/news/how-to-make-your-first-pull-request-on-github-3/).
A good starting point for understanding the high-level design of Verik is the
[language whitepaper](https://verik.io/pdf/whitepaper.pdf).

The setup for working on the Verik toolchain is very similar to the setup for working on a Verik project.
Install JDK 17 and set up IntelliJ IDEA as specified in [setup](https://verik.io/docs/setup).
In order for a pull request to be merged onto the `master` branch, it must pass all of the tests and lint checks.
Run these checks by executing the `mainCheck` gradle task.

To run a wider regression test, install the local build of the toolchain with `mainInstall` and run the `verik` gradle
task on all of the [example projects](https://github.com/frwang96/verik-examples) to check for unexpected error
messages.
This regression test, as well as the compilation and execution of the generated SystemVerilog, is
[automatically run](https://verik.io/docs/regression) when new changes are introduced.
If you have a Verik project that you are willing to share, I would love to add it to this regression test.

## Gradle Modules

Verik makes use of [gradle](https://gradle.org) for build automation.
The Verik toolchain is structured as a gradle composite build.
The final output of the build is a [gradle plugin](https://plugins.gradle.org/plugin/io.verik.verik-plugin) that
packages the importer and compiler, allowing them to be run as gradle tasks in Verik projects.
The project consists of the following gradle modules.

### verik-kotlin
Documentation for declarations from the Kotlin standard library that are shared with Verik.
This is used to generate part of the [API docs](https://verik.io/api/-verik/io.verik.kotlin/index.html) but are not used
anywhere else in the toolchain.

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
It [configures](https://verik.io/docs/reference/toolchain-config) the toolchain based on the `verik` and `verikImport`
blocks that are specified in `build.gradle.kts`.

### verik-sandbox
A sandbox project that can be used for quick development and debugging.
The toolchain is automatically recompiled when the sandbox project is run, so there is no need to execute `mainInstall`.
Verilog or SystemVerilog source code to be imported should be placed under `verik-sandbox/src/main/verilog` and Verik
source code to be compiled should be placed under `verik-sandbox/src/main/kotlin`.

## Gradle Tasks

Here are the main gradle tasks for the project.
Each module also defines its own tasks for building, testing, and reformatting.
These can be accessed from the Gradle toolbar.

- `mainCheck`: Run the tests and lint checks.
- `mainClean`: Clean all build directories.
- `mainFormat`: Reformat the source code.
- `mainGenerate`: Run the ANTLR parser generator for the importer.
- `mainInstall`: Install the gradle plugin.
- `mainTest`: Run the tests.
- `mainVerik`: Run the compiler on the sandbox project.
- `mainVerikImport`: Run the importer on the sandbox project.
