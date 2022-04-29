/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.main

import org.gradle.api.Named

abstract class TargetDomainObject(private val name: String) : Named {

    override fun getName(): String {
        return name
    }
}
