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

val exampleNames = gradle
    .includedBuild("verik-examples")
    .projectDir
    .listFiles()
    ?.filter { it.resolve("src").exists() }
    ?.map { it.name }
    ?: listOf()

val excludedExampleNames = listOf<String>()

tasks.register("test") {
    group = "verification"
    dependsOn(gradle.includedBuild("verik-compiler").task(":test"))
}

tasks.register("check") {
    group = "verification"
    dependsOn(gradle.includedBuild("verik-kotlin").task(":check"))
    dependsOn(gradle.includedBuild("verik-core").task(":check"))
    dependsOn(gradle.includedBuild("verik-compiler").task(":check"))
    dependsOn(gradle.includedBuild("verik-plugin").task(":check"))
    exampleNames.forEach {
        dependsOn(gradle.includedBuild("verik-examples").task(":$it:check"))
    }
}

tasks.register("sanity") {
    group = "verification"
    dependsOn("test")
    dependsOn("check")
    exampleNames.forEach {
        if (it !in excludedExampleNames)
            dependsOn(gradle.includedBuild("verik-examples").task(":$it:verik"))
        else
            dependsOn(gradle.includedBuild("verik-examples").task(":$it:build"))
    }
}

tasks.register("format") {
    group = "formatting"
    dependsOn(gradle.includedBuild("verik-kotlin").task(":ktlintFormat"))
    dependsOn(gradle.includedBuild("verik-core").task(":ktlintFormat"))
    dependsOn(gradle.includedBuild("verik-compiler").task(":ktlintFormat"))
    dependsOn(gradle.includedBuild("verik-plugin").task(":ktlintFormat"))
    exampleNames.forEach {
        dependsOn(gradle.includedBuild("verik-examples").task(":$it:ktlintFormat"))
    }
}

tasks.register("install") {
    group = "build"
    dependsOn(gradle.includedBuild("verik-core").task(":publishToMavenLocal"))
    dependsOn(gradle.includedBuild("verik-compiler").task(":publishToMavenLocal"))
    dependsOn(gradle.includedBuild("verik-plugin").task(":publishToMavenLocal"))
}

tasks.register("clean") {
    group = "build"
    dependsOn(gradle.includedBuild("verik-kotlin").task(":clean"))
    dependsOn(gradle.includedBuild("verik-core").task(":clean"))
    dependsOn(gradle.includedBuild("verik-compiler").task(":clean"))
    dependsOn(gradle.includedBuild("verik-plugin").task(":clean"))
    exampleNames.forEach {
        dependsOn(gradle.includedBuild("verik-examples").task(":$it:clean"))
    }
}

exampleNames.forEach {
    val upperName = it[0].toUpperCase() + it.substring(1)
    tasks.register("verik$upperName") {
        group = "verik"
        dependsOn(gradle.includedBuild("verik-examples").task(":$it:verik"))
    }
}