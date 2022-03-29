/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.target.common

/**
 * Interface for composite targets. Composite targets are target declarations that are not defined by the SystemVerilog
 * langauge and are generated in the Verik SystemVerilog package.
 */
interface CompositeTarget {

    val name: String
    val contentProlog: String
    val contentBody: String
    val contentEpilog: String
}
