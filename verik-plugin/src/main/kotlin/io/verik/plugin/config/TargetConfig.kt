/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.config

/**
 * Abstract target configuration.
 */
abstract class TargetConfig {

    abstract val toolchain: String
    abstract val timestamp: String
    abstract val projectName: String
    abstract val targetName: String
}
