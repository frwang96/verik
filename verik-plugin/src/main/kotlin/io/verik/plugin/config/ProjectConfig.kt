/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.config

import java.nio.file.Path

/**
 * Configuration for the project that is used for target generation.
 */
data class ProjectConfig(
    val toolchain: String,
    val timestamp: String,
    val projectName: String,
    val buildDir: Path
)
