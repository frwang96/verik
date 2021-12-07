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
    kotlin("jvm") version "1.5.31"
    id("org.gradle.antlr")
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
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    antlr("org.antlr:antlr4:4.9.3")
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.generateGrammarSource {
    arguments = listOf("-visitor", "-package", "io.verik.importer.antlr")
    outputDirectory = buildDir.resolve("generated-src/antlr/main/io/verik/importer/antlr")
}

tasks.compileKotlin {
    dependsOn(tasks.generateGrammarSource)
}

tasks.compileTestKotlin {
    dependsOn(tasks.generateGrammarSource)
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

tasks.runKtlintCheckOverMainSourceSet {
    dependsOn(tasks.generateGrammarSource)
}

signing {
    val publishing: PublishingExtension by project
    if (!version.toString().startsWith("local")) {
        sign(publishing.publications)
    }
}

publishing {
    publications {
        create<MavenPublication>("verik-importer") {
            from(components["java"])
            if (!version.toString().startsWith("local")) {
                artifact(tasks.getByName("sourceJar"))
                artifact(tasks.getByName("javadocJar"))
            }

            pom {
                name.set("Verik Importer")
                description.set("Importer for Verik projects")
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
