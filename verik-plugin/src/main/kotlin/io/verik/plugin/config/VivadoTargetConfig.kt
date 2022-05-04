/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.config

import java.nio.file.Path

/**
 * Configuration for a Xilinx vivado target.
 */
class VivadoTargetConfig(
    override val projectConfig: ProjectConfig,
    override val targetName: String,
    override val buildDir: Path,
    val part: String,
    val ipConfigFiles: List<Path>
) : TargetConfig()
