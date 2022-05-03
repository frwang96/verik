/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.`object`

import org.gradle.api.Action

/**
 * Interface for domain object that configures the toolchain.
 */
interface VerikDomainObject {

    var maxErrorCount: Int
    var debug: Boolean

    fun import(configure: Action<VerikImportDomainObject>)
    fun compile(configure: Action<VerikCompileDomainObject>)
}
