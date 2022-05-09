/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.domain

/**
 * Interface for domain objects that configure an Icarus Verilog target.
 */
interface IverilogTargetDomainObject : TargetDomainObject {

    var top: String
}
