/*
 * SPDX-License-Identifier: Apache-2.0
 */

import java.io.ByteArrayOutputStream

plugins {
    kotlin("jvm") version "1.5.31"
    id("org.jetbrains.changelog") version "1.3.1"
}

repositories {
    mavenCentral()
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

changelog {
    if (project.version.toString().startsWith("local")) {
        val outputStream =  ByteArrayOutputStream()
        project.exec {
            commandLine = listOf("git", "describe", "--tags", "--abbrev=0")
            standardOutput = outputStream
        }
        val version = String(outputStream.toByteArray()).trim().drop(1)
        setVersion(version)
    } else {
        setVersion(project.version)
    }
    groups.set(listOf())
}

tasks.register("mainGenerate") {
    group = "main"
    dependsOn(gradle.includedBuild("verik-importer").task(":generateGrammarSource"))
}

tasks.register("mainFormat") {
    group = "main"
    dependsOn(gradle.includedBuild("verik-kotlin").task(":ktlintFormat"))
    dependsOn(gradle.includedBuild("verik-core").task(":ktlintFormat"))
    dependsOn(gradle.includedBuild("verik-compiler").task(":ktlintFormat"))
    dependsOn(gradle.includedBuild("verik-importer").task(":ktlintFormat"))
    dependsOn(gradle.includedBuild("verik-plugin").task(":ktlintFormat"))
}

tasks.register("mainTest") {
    group = "main"
    dependsOn(gradle.includedBuild("verik-compiler").task(":test"))
    dependsOn(gradle.includedBuild("verik-importer").task(":test"))
}

tasks.register("mainCheck") {
    group = "main"
    dependsOn(gradle.includedBuild("verik-kotlin").task(":check"))
    dependsOn(gradle.includedBuild("verik-core").task(":check"))
    dependsOn(gradle.includedBuild("verik-compiler").task(":check"))
    dependsOn(gradle.includedBuild("verik-importer").task(":check"))
    dependsOn(gradle.includedBuild("verik-plugin").task(":check"))
}

tasks.register("mainInstall") {
    group = "main"
    dependsOn(gradle.includedBuild("verik-core").task(":publishToMavenLocal"))
    dependsOn(gradle.includedBuild("verik-compiler").task(":publishToMavenLocal"))
    dependsOn(gradle.includedBuild("verik-importer").task(":publishToMavenLocal"))
    dependsOn(gradle.includedBuild("verik-plugin").task(":publishToMavenLocal"))
}

tasks.register("mainClean") {
    group = "main"
    dependsOn(gradle.includedBuild("verik-kotlin").task(":clean"))
    dependsOn(gradle.includedBuild("verik-core").task(":clean"))
    dependsOn(gradle.includedBuild("verik-compiler").task(":clean"))
    dependsOn(gradle.includedBuild("verik-importer").task(":clean"))
    dependsOn(gradle.includedBuild("verik-plugin").task(":clean"))
    dependsOn(gradle.includedBuild("verik-sandbox").task(":clean"))
}

tasks.register("mainVerikBuild") {
    group = "main"
    dependsOn(gradle.includedBuild("verik-sandbox").task(":verikBuild"))
}

tasks.register("mainVerikCompile") {
    group = "main"
    dependsOn(gradle.includedBuild("verik-sandbox").task(":verikCompile"))
}

tasks.register("mainVerikImport") {
    group = "main"
    dependsOn(gradle.includedBuild("verik-sandbox").task(":verikImport"))
}
