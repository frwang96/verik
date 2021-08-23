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

group = "io.verik"

plugins {
    kotlin("jvm") version "1.4.32"
    id("java-gradle-plugin")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "0.15.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.32")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.10")
    implementation("io.verik:verik-compiler:$version")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
}

gradlePlugin {
    plugins {
        create("verik-plugin") {
            id = "io.verik.verik-plugin"
            displayName = "Verik Plugin"
            description = "Plugin for the Verik compiler"
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
