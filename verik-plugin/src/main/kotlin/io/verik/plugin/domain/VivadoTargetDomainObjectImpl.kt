/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.domain

import java.nio.file.Path

/**
 * Implementation for domain objects that configure a vivado target.
 */
class VivadoTargetDomainObjectImpl : VivadoTargetDomainObject {

    override var name: String = "vivado"
    override var part: String = ""
    override var ipConfigFiles: List<Path> = listOf()
}
