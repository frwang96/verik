/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.domain

/**
 * Interface for domain objects that configure a Metrics dsim simulation.
 */
interface DsimSimDomainObject {

    var name: String
    var simTop: String
    val plusArgs: HashMap<String, String>
}
