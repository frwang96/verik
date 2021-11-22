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

val sanityNames = gradle
    .includedBuild("verik-sanity")
    .projectDir
    .listFiles()
    ?.filter { it.resolve("src").exists() }
    ?.map { it.name }
    ?: listOf()

val excludedSanityNames = listOf<String>()

tasks.register("test") {
    group = "verification"
    dependsOn(gradle.includedBuild("verik-compiler").task(":test"))
    dependsOn(gradle.includedBuild("verik-import").task(":test"))
}

tasks.register("check") {
    group = "verification"
    dependsOn(gradle.includedBuild("verik-kotlin").task(":check"))
    dependsOn(gradle.includedBuild("verik-core").task(":check"))
    dependsOn(gradle.includedBuild("verik-compiler").task(":check"))
    dependsOn(gradle.includedBuild("verik-import").task(":check"))
    dependsOn(gradle.includedBuild("verik-plugin").task(":check"))
    sanityNames.forEach {
        dependsOn(gradle.includedBuild("verik-sanity").task(":$it:check"))
    }
}

tasks.register("sanity") {
    group = "verification"
    dependsOn("test")
    dependsOn("check")
    sanityNames.forEach {
        if (it !in excludedSanityNames)
            dependsOn(gradle.includedBuild("verik-sanity").task(":$it:verik"))
        else
            dependsOn(gradle.includedBuild("verik-sanity").task(":$it:build"))
    }
}

tasks.register("format") {
    group = "formatting"
    dependsOn(gradle.includedBuild("verik-kotlin").task(":ktlintFormat"))
    dependsOn(gradle.includedBuild("verik-core").task(":ktlintFormat"))
    dependsOn(gradle.includedBuild("verik-compiler").task(":ktlintFormat"))
    dependsOn(gradle.includedBuild("verik-import").task(":ktlintFormat"))
    dependsOn(gradle.includedBuild("verik-plugin").task(":ktlintFormat"))
    sanityNames.forEach {
        dependsOn(gradle.includedBuild("verik-sanity").task(":$it:ktlintFormat"))
    }
}

tasks.register("install") {
    group = "build"
    dependsOn(gradle.includedBuild("verik-core").task(":publishToMavenLocal"))
    dependsOn(gradle.includedBuild("verik-compiler").task(":publishToMavenLocal"))
    dependsOn(gradle.includedBuild("verik-import").task(":publishToMavenLocal"))
    dependsOn(gradle.includedBuild("verik-plugin").task(":publishToMavenLocal"))
}

tasks.register("clean") {
    group = "build"
    dependsOn(gradle.includedBuild("verik-kotlin").task(":clean"))
    dependsOn(gradle.includedBuild("verik-core").task(":clean"))
    dependsOn(gradle.includedBuild("verik-compiler").task(":clean"))
    dependsOn(gradle.includedBuild("verik-import").task(":clean"))
    dependsOn(gradle.includedBuild("verik-plugin").task(":clean"))
    sanityNames.forEach {
        dependsOn(gradle.includedBuild("verik-sanity").task(":$it:clean"))
    }
}

sanityNames.forEach {
    val upperName = it[0].toUpperCase() + it.substring(1)
    tasks.register("verik$upperName") {
        group = "verik"
        dependsOn(gradle.includedBuild("verik-sanity").task(":$it:verik"))
    }
}