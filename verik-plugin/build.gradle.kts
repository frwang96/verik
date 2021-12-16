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

import java.io.ByteArrayOutputStream

group = "io.verik"

plugins {
    kotlin("jvm") version "1.5.31"
    id("java-gradle-plugin")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "0.15.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.31")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
    @Suppress("GradlePackageUpdate")
    implementation("io.verik:verik-compiler:$version")
    @Suppress("GradlePackageUpdate")
    implementation("io.verik:verik-importer:$version")
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.register("writeProperties", WriteProperties::class) {
    property("version", version)
    property("toolchain") {
        if (version.toString().startsWith("local")) {
            val outputStream = ByteArrayOutputStream()
            project.exec {
                commandLine = listOf("git", "describe", "--long", "--tags", "--dirty", "--always")
                standardOutput = outputStream
            }
            "verik ${String(outputStream.toByteArray()).trim()}"
        } else {
            "verik v$version"
        }
    }
    outputFile = buildDir.resolve("generated/verik-plugin.properties")
}

tasks.processResources {
    from(files(tasks.getByName("writeProperties")))
}

gradlePlugin {
    plugins {
        create("verik-plugin") {
            id = "io.verik.verik-plugin"
            displayName = "Verik Plugin"
            description = "Plugin for the Verik toolchain"
            implementationClass = "io.verik.plugin.VerikPlugin"
        }
    }
}

pluginBundle {
    website = "https://verik.io"
    vcsUrl = "https://github.com/frwang96/verik.git"
    tags = listOf("hardware")
    mavenCoordinates {
        groupId = group
    }
}
