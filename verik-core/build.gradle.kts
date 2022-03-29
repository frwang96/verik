/*
 * SPDX-License-Identifier: Apache-2.0
 */

group = "io.verik"

plugins {
    kotlin("jvm") version "1.5.31"
    id("org.jetbrains.dokka") version "1.6.10"
    id("signing")
    id("maven-publish")
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
}

repositories {
    mavenCentral()
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.31")
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.register<Jar>("sourceJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from(tasks.dokkaJavadoc)
}

signing {
    val publishing: PublishingExtension by project
    if (!version.toString().startsWith("local")) {
        sign(publishing.publications)
    }
}

publishing {
    publications {
        create<MavenPublication>("verik-core") {
            from(components["java"])
            artifact(tasks.getByName("javadocJar"))
            artifact(tasks.getByName("sourceJar"))

            pom {
                name.set("Verik Core")
                description.set("Core library for Verik projects")
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
