/*
 * Copyright 2020 Francis Wang
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

plugins {
    kotlin("jvm") version "1.4.0"
    id("org.jetbrains.dokka") version "1.4.0-rc"
}

group = "verik"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

sourceSets.main {
    java.srcDirs("../common/src/main/kotlin")
    java.srcDirs("../stubs/src/main/kotlin")
}

tasks.dokkaHtml {
    dokkaSourceSets {
        configureEach {
            moduleDisplayName = "verik"
            includes = listOf("res/module.md")
        }
    }
}

tasks.build {
    dependsOn(tasks.dokkaHtml)
}
