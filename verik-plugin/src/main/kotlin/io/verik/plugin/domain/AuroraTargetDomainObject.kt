/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.domain

import java.nio.file.Path

/**
 * Interface for domain objects that configure an aurora target.
 */
interface AuroraTargetDomainObject : TargetDomainObject {

    var compileTops: List<String>
    var dpiLibs: List<Path>
}
