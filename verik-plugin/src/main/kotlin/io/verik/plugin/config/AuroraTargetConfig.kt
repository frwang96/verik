/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.config

import java.nio.file.Path

/**
 * Configuration for an aurora target.
 */
class AuroraTargetConfig(
    override val toolchain: String,
    override val timestamp: String,
    override val projectName: String,
    override val targetName: String,
    val compileTops: List<String>,
    val dpiLibs: List<Path>
) : TargetConfig()
