/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.sv

import io.verik.compiler.ast.element.declaration.common.EAbstractFunction
import io.verik.compiler.target.common.Target

abstract class EAbstractProceduralBlock : EAbstractFunction() {

    override var type = Target.C_Void.toType()
}
