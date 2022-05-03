/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.`object`

/**
 * Implementation for domain object that configures the compiler.
 */
class VerikCompileDomainObjectImpl : VerikCompileDomainObject {

    override var timescale: String = "1ns / 1ns"
    override var entryPoints: List<String> = listOf()
    override var enableDeadCodeElimination: Boolean = true
    override var labelLines: Boolean = true
    override var indentLength: Int = 4
    override var wrapLength: Int = 120
    override var suppressedWarnings: List<String> = listOf()
    override var promotedWarnings: List<String> = listOf()
}
