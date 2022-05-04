/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.domain

import java.nio.file.Path

/**
 * Implementation for domain objects that configure an aurora target.
 */
class AuroraTargetDomainObjectImpl : AuroraTargetDomainObject {

    override var name: String = "aurora"
    override var compileTops: List<String> = listOf()
    override var dpiLibs: List<Path> = listOf()
}
