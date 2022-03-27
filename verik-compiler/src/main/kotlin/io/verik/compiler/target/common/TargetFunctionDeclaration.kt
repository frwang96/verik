/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.target.common

sealed class TargetFunctionDeclaration : TargetDeclaration

class PrimitiveTargetFunctionDeclaration(
    override val parent: TargetDeclaration,
    override var name: String
) : TargetFunctionDeclaration()

class CompositeTargetFunctionDeclaration(
    override val parent: TargetDeclaration,
    override var name: String,
    override val contentBody: String,
    val isConstructor: Boolean
) : TargetFunctionDeclaration(), CompositeTarget {

    override val contentProlog = ""
    override val contentEpilog = ""
}
