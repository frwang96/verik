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
    id("org.jetbrains.dokka") version "1.5.0"
    id("java-gradle-plugin")
    id("signing")
    id("com.gradle.plugin-publish") version "0.15.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.32")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.10")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.4.10")
    implementation("io.verik:verik-core:$version")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
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

tasks.test {
    useJUnitPlatform()
    systemProperties["junit.jupiter.execution.parallel.enabled"] = true
    systemProperties["junit.jupiter.execution.parallel.mode.default"] = "concurrent"
}

tasks.register<Jar>("sourceJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from(tasks.dokkaJavadoc)
}

artifacts {
    archives(tasks.getByName("sourceJar"))
    archives(tasks.getByName("javadocJar"))
}

signing {
    sign(configurations.archives.get())
}

gradlePlugin {
    plugins {
        create("verik-plugin") {
            id = "io.verik.verik-plugin"
            displayName = "Verik"
            description = "Plugin for the Verik compiler"
            implementationClass = "io.verik.plugin.VerikPlugin"
        }
    }
}

pluginBundle {
    website = "https://verik.io"
    vcsUrl = "https://github.com/frwang96/verik.git"
    tags = listOf("hardware")
}
