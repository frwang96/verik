/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.domain

/**
 * Interface for domain objects that configure the toolchain.
 */
interface VerikDomainObject {

    var maxErrorCount: Int
    var debug: Boolean

    fun import(configure: VerikImportDomainObject.() -> Unit)
    fun compile(configure: VerikCompileDomainObject.() -> Unit)
    fun aurora(configure: AuroraTargetDomainObject.() -> Unit)
    fun vivado(configure: VivadoTargetDomainObject.() -> Unit)
}
