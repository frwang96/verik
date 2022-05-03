/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.`object`

/**
 * Interface for domain object that configures the toolchain.
 */
interface VerikDomainObject {

    var maxErrorCount: Int
    var debug: Boolean

    fun import(configure: VerikImportDomainObject.() -> Unit)
    fun compile(configure: VerikCompileDomainObject.() -> Unit)
}
