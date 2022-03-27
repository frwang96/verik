/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.specialize

import io.verik.compiler.ast.element.declaration.common.EDeclaration

abstract class SpecializerSubstage {

    abstract fun process(declaration: EDeclaration, typeParameterBinding: TypeParameterBinding)
}
