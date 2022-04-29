/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.main

import io.verik.plugin.target.aurora.AuroraTargetDomainObject
import io.verik.plugin.target.vivado.VivadoTargetDomainObject
import org.gradle.api.Action
import org.gradle.api.ExtensiblePolymorphicDomainObjectContainer
import org.gradle.api.PolymorphicDomainObjectContainer

class TargetContainer(
    delegate: ExtensiblePolymorphicDomainObjectContainer<TargetDomainObject>
) : PolymorphicDomainObjectContainer<TargetDomainObject> by delegate {

    init {
        delegate.registerFactory(AuroraTargetDomainObject::class.java) {
            AuroraTargetDomainObject(it)
        }
        delegate.registerFactory(VivadoTargetDomainObject::class.java) {
            VivadoTargetDomainObject(it)
        }
    }

    @Suppress("unused")
    fun aurora(name: String = "aurora", action: Action<in AuroraTargetDomainObject>) {
        create(name, AuroraTargetDomainObject::class.java) {
            action.execute(it)
        }
    }

    @Suppress("unused")
    fun vivado(name: String = "vivado", action: Action<in VivadoTargetDomainObject>) {
        create(name, VivadoTargetDomainObject::class.java) {
            action.execute(it)
        }
    }
}
