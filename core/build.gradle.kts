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
    kotlin("jvm") version "1.3.72"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.3.72"
    antlr
}

group = "com.verik"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")
    implementation("com.charleskorn.kaml:kaml:0.18.1")
    implementation("org.snakeyaml:snakeyaml-engine:2.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
    antlr("org.antlr:antlr4:4.8")
}

tasks.register<Copy>("copyGrammarSource") {
    from("src/main/antlr/com/verik/antlr")
    into("build/generated-src/antlr/main/com/verik/antlr")
}

tasks.generateGrammarSource {
    arguments = listOf("-package", "com.verik.antlr", "-lib", "build/generated-src/antlr/main/com/verik/antlr")
    dependsOn(tasks.getByName("copyGrammarSource"))
}

tasks.compileJava {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
    dependsOn(tasks.generateGrammarSource)
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
    dependsOn(tasks.generateGrammarSource)
}

tasks.compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.test {
    useJUnitPlatform()
    systemProperties["junit.jupiter.execution.parallel.enabled"] = true
    systemProperties["junit.jupiter.execution.parallel.mode.default"] = "concurrent"
}

tasks.jar {
    manifest.attributes["Main-Class"] = "com.verik.core.MainKt"
    configurations["compileClasspath"].forEach { from(zipTree(it.absoluteFile)) }
}
