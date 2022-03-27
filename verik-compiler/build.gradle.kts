/*
 * SPDX-License-Identifier: Apache-2.0
 */

import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jlleitschuh.gradle.ktlint.KtlintExtension

group = "io.verik"

plugins {
    kotlin("jvm") version "1.5.31"
    id("org.jetbrains.dokka") version "1.6.0"
    id("signing")
    id("maven-publish")
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.31")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.31")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.5.31")
    @Suppress("GradlePackageUpdate")
    implementation("io.verik:verik-core:$version")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.test {
    inputs.dir(projectDir.resolve("regression"))
    useJUnitPlatform()
    systemProperties["junit.jupiter.execution.parallel.enabled"] = true
    systemProperties["junit.jupiter.execution.parallel.mode.default"] = "concurrent"
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
    }
    setForkEvery(24)
}

tasks.register<Jar>("sourceJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from(tasks.dokkaJavadoc)
}

configure<KtlintExtension> {
    disabledRules.set(listOf("no-wildcard-imports"))
}

signing {
    val publishing: PublishingExtension by project
    if (!version.toString().startsWith("local")) {
        sign(publishing.publications)
    }
}

publishing {
    publications {
        create<MavenPublication>("verik-compiler") {
            from(components["java"])
            if (!version.toString().startsWith("local")) {
                artifact(tasks.getByName("sourceJar"))
                artifact(tasks.getByName("javadocJar"))
            }

            pom {
                name.set("Verik Compiler")
                description.set("Compiler for Verik projects")
                url.set("https://verik.io")
                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
                developers {
                    developer {
                        name.set("Francis Wang")
                        email.set("emailforfrancis@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git:https://github.com/frwang96/verik.git")
                    developerConnection.set("scm:git:ssh:https://github.com/frwang96/verik.git")
                    url.set("https://github.com/frwang96/verik/tree/master")
                }
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}
