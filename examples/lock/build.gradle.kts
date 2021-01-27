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

plugins {
    kotlin("jvm") version "1.4.20"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    val verik_home = System.getenv("VERIK_HOME") ?: throw Exception("environment variable VERIK_HOME not set")
    implementation(files("$verik_home/verik/build/libs/verik.jar"))
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.jar {
    archiveBaseName.set("out")
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    configurations["compileClasspath"].forEach { from(zipTree(it.absoluteFile)) }
}
