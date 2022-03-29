/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.common

import io.verik.compiler.ast.element.declaration.common.EDeclaration

/**
 * Interface for elements that contain [declarations][EDeclaration].
 */
interface DeclarationContainer {

    fun replaceChild(oldDeclaration: EDeclaration, newDeclaration: EDeclaration): Boolean
}
