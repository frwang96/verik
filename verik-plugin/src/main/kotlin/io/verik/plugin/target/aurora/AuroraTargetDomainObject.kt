/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.target.aurora

import io.verik.plugin.main.TargetDomainObject

class AuroraTargetDomainObject(name: String) : TargetDomainObject(name) {

    var compileFlags: String = ""
}
