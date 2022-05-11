/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.domain

import java.nio.file.Path

/**
 * Interface for domain objects that configure a Metrics dsim target.
 */
interface DsimTargetDomainObject : TargetDomainObject {

    var compileTops: List<String>
    var extraFiles: List<Path>
    var extraIncludeDirs: List<Path>
    var dpiLibs: List<Path>

    fun sim(configure: DsimSimDomainObject.() -> Unit)
}
