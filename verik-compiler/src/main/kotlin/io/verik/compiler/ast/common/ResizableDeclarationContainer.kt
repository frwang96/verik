/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.common

import io.verik.compiler.ast.element.declaration.common.EDeclaration

interface ResizableDeclarationContainer : DeclarationContainer {

    fun insertChild(declaration: EDeclaration)

    fun insertChildBefore(oldDeclaration: EDeclaration, newDeclaration: EDeclaration)
}
