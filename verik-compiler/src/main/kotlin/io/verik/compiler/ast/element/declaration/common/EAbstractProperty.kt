/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.common

import io.verik.compiler.message.SourceLocation

abstract class EAbstractProperty : EDeclaration() {

    abstract val endLocation: SourceLocation
}
