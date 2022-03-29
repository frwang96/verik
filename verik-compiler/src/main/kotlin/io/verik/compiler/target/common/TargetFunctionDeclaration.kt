/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.target.common

/**
 * A target declaration that represents a function.
 */
sealed class TargetFunctionDeclaration : TargetDeclaration

/**
 * A target function declaration that is defined by the SystemVerilog language.
 */
class PrimitiveTargetFunctionDeclaration(
    override val parent: TargetDeclaration,
    override var name: String
) : TargetFunctionDeclaration()

/**
 * A target function declaration that is generated in the Verik SystemVerilog package.
 */
class CompositeTargetFunctionDeclaration(
    override val parent: TargetDeclaration,
    override var name: String,
    override val contentBody: String,
    val isConstructor: Boolean
) : TargetFunctionDeclaration(), CompositeTarget {

    override val contentProlog = ""
    override val contentEpilog = ""
}
