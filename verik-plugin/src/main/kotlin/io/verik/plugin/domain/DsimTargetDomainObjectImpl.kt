/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.domain

import org.gradle.api.Action
import java.nio.file.Path

/**
 * Implementation for domain objects that configure a Metrics dsim target.
 */
class DsimTargetDomainObjectImpl : DsimTargetDomainObject {

    override var name: String = "dsim"
    override var compileTops: List<String> = listOf()
    override var extraIncludeDirs: List<Path> = listOf()
    override var extraFiles: List<Path> = listOf()
    override var dpiLibs: List<Path> = listOf()

    val simDomainObjects = ArrayList<DsimSimDomainObjectImpl>()

    override fun sim(configure: DsimSimDomainObject.() -> Unit) {
        val action = Action<DsimSimDomainObject> { it.configure() }
        val domainObject = DsimSimDomainObjectImpl()
        action.execute(domainObject)
        simDomainObjects.add(domainObject)
    }
}
