/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.domain

import java.nio.file.Path

/**
 * Implementation for domain objects that configure a metrics Dsim target.
 */
class DsimTargetDomainObjectImpl : DsimTargetDomainObject {

    override var name: String = "dsim"
    override var compileTops: List<String> = listOf()
    override var dpiLibs: List<Path> = listOf()
}
