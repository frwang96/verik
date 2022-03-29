/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.main

import java.nio.file.Path

/**
 * Overall configuration for the compiler.
 */
data class VerikConfig(
    val toolchain: String,
    val timestamp: String,
    val projectName: String,
    val buildDir: Path,
    val sourceSetConfigs: List<SourceSetConfig>,
    val timescale: String,
    val entryPoints: List<String>,
    val enableDeadCodeElimination: Boolean,
    val labelLines: Boolean,
    val indentLength: Int,
    val wrapLength: Int,
    val suppressedWarnings: List<String>,
    val promotedWarnings: List<String>,
    val maxErrorCount: Int,
    val debug: Boolean
) {

    val outputSourceDir: Path = buildDir.resolve("src")
}
