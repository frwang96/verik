/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.domain

/**
 * Implementation for domain objects that configure a Cadence xrun target.
 */
class XrunTargetDomainObjectImpl : XrunTargetDomainObject {

    override var name = "xrun"
    override var top = ""
}
