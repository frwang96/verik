/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.target.vivado

import io.verik.plugin.main.TargetDomainObject

class VivadoTargetDomainObject(name: String) : TargetDomainObject(name) {

    var isSimulation: Boolean = false
}
