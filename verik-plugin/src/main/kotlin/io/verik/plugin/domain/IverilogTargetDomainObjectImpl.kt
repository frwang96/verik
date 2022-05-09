/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.domain

/**
 * Implementation for domain objects that configure an Icarus Verilog target.
 */
class IverilogTargetDomainObjectImpl : IverilogTargetDomainObject {

    override var name = "iverilog"
    override var top = ""
}
