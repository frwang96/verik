/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.property

import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.EExpression

/**
 * Entry of a when expression.
 */
class WhenEntry(
    var conditions: ArrayList<EExpression>,
    var body: EBlockExpression
)
