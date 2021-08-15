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
    id("maven-publish")
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
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

signing {
    val publishing: PublishingExtension by project
    sign(publishing.publications)
}

publishing {
    publications {
        create<MavenPublication>("verik-compiler") {
            from(components["java"])
            artifact(tasks.getByName("sourceJar"))
            artifact(tasks.getByName("javadocJar"))

            pom {
                name.set("Verik Compiler")
                description.set("Compiler for Verik projects")
                url.set("https://verik.io")
                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0")
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

gradlePlugin {
    isAutomatedPublishing = false
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
