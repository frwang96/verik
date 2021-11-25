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

import io.verik.plugin.VerikImporterPluginExtension
import io.verik.plugin.VerikPluginExtension
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import java.nio.file.Files
import kotlin.streams.toList

plugins {
    id("io.verik.verik-plugin") apply false
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0" apply false
}

subprojects {
    group = "io.verik"
    version = "local-SNAPSHOT"
    apply(plugin = "io.verik.verik-plugin")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    configure<KtlintExtension> {
        disabledRules.set(listOf("no-wildcard-imports"))
    }
    dependencies {
        val implementation by configurations
        implementation("io.verik:verik-core:$version")
    }
    repositories {
        mavenCentral()
    }
    configure<VerikPluginExtension> {
        debug = true
        labelLines = true
    }
    configure<VerikImporterPluginExtension> {
        val verilogSrcDir = projectDir.resolve("src/main/verilog").toPath()
        if (Files.exists(verilogSrcDir)) {
            importedFiles = Files.walk(verilogSrcDir).toList()
                .filter { it.fileName.toString().endsWith(".v") }
        }
        debug = true
    }
}