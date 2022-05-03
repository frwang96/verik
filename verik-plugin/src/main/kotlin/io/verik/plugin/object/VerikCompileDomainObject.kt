/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.`object`

/**
 * Interface for domain object that configures the compiler.
 */
interface VerikCompileDomainObject {

    var timescale: String
    var entryPoints: List<String>
    var enableDeadCodeElimination: Boolean
    var labelLines: Boolean
    var indentLength: Int
    var wrapLength: Int
    var suppressedWarnings: List<String>
    var promotedWarnings: List<String>
}
