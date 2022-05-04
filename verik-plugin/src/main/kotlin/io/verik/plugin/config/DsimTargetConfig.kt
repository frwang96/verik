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
    override val targetName: String,
    override val buildDir: Path,
    val compileTops: List<String>,
    val dpiLibs: List<Path>
) : TargetConfig()
