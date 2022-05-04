/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.domain

import org.gradle.api.Action

/**
 * Implementation for domain objects that configure the toolchain.
 */
class VerikDomainObjectImpl : VerikDomainObject {

    override var maxErrorCount: Int = 60
    override var debug: Boolean = false

    val importDomainObject = VerikImportDomainObjectImpl()
    val compileDomainObject = VerikCompileDomainObjectImpl()
    val dsimDomainObjects = ArrayList<DsimTargetDomainObjectImpl>()
    val vivadoDomainObjects = ArrayList<VivadoTargetDomainObjectImpl>()

    override fun import(configure: VerikImportDomainObject.() -> Unit) {
        val action = Action<VerikImportDomainObject> { it.configure() }
        action.execute(importDomainObject)
    }

    override fun compile(configure: VerikCompileDomainObject.() -> Unit) {
        val action = Action<VerikCompileDomainObject> { it.configure() }
        action.execute(compileDomainObject)
    }

    override fun dsim(configure: DsimTargetDomainObject.() -> Unit) {
        val action = Action<DsimTargetDomainObject> { it.configure() }
        val domainObject = DsimTargetDomainObjectImpl()
        action.execute(domainObject)
        dsimDomainObjects.add(domainObject)
    }

    override fun vivado(configure: VivadoTargetDomainObject.() -> Unit) {
        val action = Action<VivadoTargetDomainObject> { it.configure() }
        val domainObject = VivadoTargetDomainObjectImpl()
        action.execute(domainObject)
        vivadoDomainObjects.add(domainObject)
    }
}
