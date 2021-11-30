/*
 * Copyright (c) 2021 Francis Wang
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

package io.verik.compiler.main

import java.nio.file.Path

data class VerikConfig(
    val version: String,
    val timestamp: String,
    val projectName: String,
    val projectDir: Path,
    val buildDir: Path,
    val sourceSetConfigs: List<SourceSetConfig>,
    val timescale: String,
    val debug: Boolean,
    val suppressedWarnings: List<String>,
    val promotedWarnings: List<String>,
    val maxErrorCount: Int,
    val labelSourceLocations: Boolean,
    val wrapLength: Int,
    val indentLength: Int,
    val enableDeadCodeElimination: Boolean
) {

    val outputSourceDir: Path = buildDir.resolve("src")
}
