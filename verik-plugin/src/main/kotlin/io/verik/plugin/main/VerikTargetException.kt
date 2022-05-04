/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.main

import io.verik.plugin.config.TargetConfig

/**
 * Exception to be thrown for errors that arise during target generation.
 */
class VerikTargetException(
    targetConfig: TargetConfig,
    override val message: String
) : Exception() {

    val targetName: String = targetConfig.targetName
}
