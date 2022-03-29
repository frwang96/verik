/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.target.common

/**
 * A target declaration that represents a property.
 */
class TargetPropertyDeclaration(
    override val parent: TargetDeclaration?,
    override var name: String
) : TargetDeclaration
