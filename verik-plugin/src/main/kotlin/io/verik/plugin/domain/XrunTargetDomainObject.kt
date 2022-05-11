/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.domain

/**
 * Interface for domain objects that configure a Cadence xrun target.
 */
interface XrunTargetDomainObject : TargetDomainObject {

    var top: String
}
