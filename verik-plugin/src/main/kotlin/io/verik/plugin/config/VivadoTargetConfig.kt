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
    override val name: String,
    override val buildDir: Path,
    val part: String,
    val ipConfigFiles: List<Path>,
    val simTop: String,
    val synthTop: String,
    val constraintsFile: Path?
) : TargetConfig()
