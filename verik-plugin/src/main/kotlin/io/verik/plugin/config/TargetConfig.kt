/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.config

import java.nio.file.Path

/**
 * Abstract target configuration.
 */
abstract class TargetConfig {

    abstract val projectConfig: ProjectConfig
    abstract val targetName: String
    abstract val buildDir: Path
}
