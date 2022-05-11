/*
 * SPDX-License-Identifier: Apache-2.0
 */

import java.nio.file.Files

group = "io.verik"

plugins {
    kotlin("jvm") version "1.5.31"
    id("io.verik.verik-plugin")
}

repositories {
    mavenCentral()
}

verik {
    debug = true
    import {
        val verilogSrcDir = projectDir.resolve("src/main/verilog").toPath()
        if (Files.exists(verilogSrcDir)) {
            importedFiles = Files.walk(verilogSrcDir).toList()
                .filter { it.toFile().extension in listOf("v", "sv") }
        }
    }
    compile {
        enableDeadCodeElimination = false
    }
    dsim {
        compileTops = listOf("Top")
        sim {
            name = "sim"
        }
    }
    iverilog {
        top = "Top"
    }
    vivado {
        part = "xc7a100tcsg324-3"
        simTop = "Top"
    }
    xrun {
        top = "Top"
    }
}
