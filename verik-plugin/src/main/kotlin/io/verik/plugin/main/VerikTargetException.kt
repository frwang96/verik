/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.main

import io.verik.plugin.config.TargetConfig
import io.verik.plugin.domain.TargetDomainObject

/**
 * Exception to be thrown for errors that arise during target generation.
 */
class VerikTargetException private constructor(
    val targetName: String,
    override val message: String
) : Exception() {

    constructor(domainObject: TargetDomainObject, message: String) : this(domainObject.name, message)

    constructor(targetConfig: TargetConfig, message: String) : this(targetConfig.name, message)
}
