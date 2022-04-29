/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.main

/**
 * Plugin extension class that is used to configure the compiler.
 */
abstract class VerikCompilerPluginExtension {

    var timescale: String = "1ns / 1ns"
    var entryPoints: ArrayList<String> = ArrayList()
    var enableDeadCodeElimination: Boolean = true
    var labelLines: Boolean = true
    var indentLength: Int = 4
    var wrapLength: Int = 120
    var suppressedWarnings: ArrayList<String> = ArrayList()
    var promotedWarnings: ArrayList<String> = ArrayList()
    var maxErrorCount: Int = 60
    var debug: Boolean = false
}
