/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.domain

/**
 * Implementation for domain objects that configure a Metrics dsim simulation.
 */
class DsimSimDomainObjectImpl : DsimSimDomainObject {

    override var name: String = ""
    override var runTop: String = ""
    override val plusArgs: HashMap<String, String> = HashMap()
}
