/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.config

import java.nio.file.Path

/**
 * Configuration for a Metrics dsim target.
 */
class DsimTargetConfig(
    override val projectConfig: ProjectConfig,
    override val name: String,
    override val buildDir: Path,
    val compileTops: List<String>,
    val extraIncludeDirs: List<Path>,
    val extraFiles: List<Path>,
    val dpiLibs: List<Path>,
    val simConfigs: List<DsimSimConfig>
) : TargetConfig()
