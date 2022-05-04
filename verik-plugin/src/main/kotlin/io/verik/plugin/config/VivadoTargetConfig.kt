/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.config

import java.nio.file.Path

/**
 * Configuration for a vivado target.
 */
class VivadoTargetConfig(
    override val toolchain: String,
    override val timestamp: String,
    override val projectName: String,
    override val targetName: String,
    val part: String,
    val ipConfigFiles: List<Path>
) : TargetConfig()
