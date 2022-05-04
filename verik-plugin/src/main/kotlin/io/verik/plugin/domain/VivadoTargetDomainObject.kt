/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.domain

import java.nio.file.Path

/**
 * Interface for domain objects that configure a vivado target.
 */
interface VivadoTargetDomainObject : TargetDomainObject {

    var part: String
    var ipConfigFiles: List<Path>
}
