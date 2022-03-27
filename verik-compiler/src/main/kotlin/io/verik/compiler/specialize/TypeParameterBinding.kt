/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.specialize

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.declaration.common.EDeclaration

data class TypeParameterBinding(
    val declaration: EDeclaration,
    val typeArguments: List<Type>
)
