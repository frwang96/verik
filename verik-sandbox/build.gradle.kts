/*
 * Copyright (c) 2020 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.nio.file.Files

group = "io.verik"

plugins {
    id("io.verik.verik-plugin")
}

repositories {
    mavenCentral()
}

verik {
    debug = true
}

verikImport {
    val verilogSrcDir = projectDir.resolve("src/main/verilog").toPath()
    if (Files.exists(verilogSrcDir)) {
        importedFiles = Files.walk(verilogSrcDir).toList()
            .filter { it.fileName.toString().endsWith(".v") }
    }
    debug = true
}
