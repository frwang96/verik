/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.common

import io.verik.importer.ast.element.declaration.EDeclaration

interface DeclarationContainer {

    fun replaceChild(oldDeclaration: EDeclaration, newDeclaration: EDeclaration): Boolean
}
