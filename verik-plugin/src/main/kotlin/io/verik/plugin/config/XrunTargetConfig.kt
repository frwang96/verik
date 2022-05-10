/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.config

import java.nio.file.Path

/**
 * Configuration for a Cadence xrun target.
 */
class XrunTargetConfig(
    override val projectConfig: ProjectConfig,
    override val name: String,
    override val buildDir: Path,
    val top: String
) : TargetConfig()
