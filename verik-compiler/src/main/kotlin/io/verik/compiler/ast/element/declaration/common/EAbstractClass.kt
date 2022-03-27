/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.common

import io.verik.compiler.ast.common.Type
import io.verik.compiler.message.SourceLocation

abstract class EAbstractClass : EClassifier() {

    abstract val bodyStartLocation: SourceLocation
    abstract val bodyEndLocation: SourceLocation

    abstract var superType: Type
}
