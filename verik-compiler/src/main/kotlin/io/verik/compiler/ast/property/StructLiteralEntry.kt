/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.property

import io.verik.compiler.ast.common.Declaration
import io.verik.compiler.ast.element.expression.common.EExpression

class StructLiteralEntry(
    val reference: Declaration,
    var expression: EExpression
)
