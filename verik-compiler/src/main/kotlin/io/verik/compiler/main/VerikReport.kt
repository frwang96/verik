/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.main

/**
 * Report data structure that stores some statistics.
 */
class VerikReport {

    var entryPoints: List<String> = listOf()
    var counts: List<Pair<String, Int>> = listOf()
}
