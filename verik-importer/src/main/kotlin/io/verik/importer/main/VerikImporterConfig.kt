/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.main

import java.nio.file.Path

/**
 * Overall configuration for the importer.
 */
data class VerikImporterConfig(
    val toolchain: String,
    val timestamp: String,
    val projectName: String,
    val buildDir: Path,
    val importedFiles: List<Path>,
    val includeDirs: List<Path>,
    val enablePreprocessorOutput: Boolean,
    val suppressedWarnings: List<String>,
    val promotedWarnings: List<String>,
    val maxErrorCount: Int,
    val debug: Boolean
) {

    val rootPackageName = "imported"
    val rootPackageOutputPath: Path = buildDir.resolve("src/$rootPackageName")
}
