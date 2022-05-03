/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.`object`

import org.gradle.api.Action

/**
 * Implementation for domain object that configures the toolchain.
 */
class VerikDomainObjectImpl : VerikDomainObject {

    override var maxErrorCount: Int = 60
    override var debug: Boolean = false

    val import = VerikImportDomainObjectImpl()
    val compile = VerikCompileDomainObjectImpl()

    override fun import(configure: VerikImportDomainObject.() -> Unit) {
        val action = Action<VerikImportDomainObject> { it.configure() }
        action.execute(import)
    }

    override fun compile(configure: VerikCompileDomainObject.() -> Unit) {
        val action = Action<VerikCompileDomainObject> { it.configure() }
        action.execute(compile)
    }
}
