/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.common

import io.verik.compiler.ast.element.expression.common.EExpression

/**
 * Interface for elements that contain [expressions][EExpression].
 */
interface ExpressionContainer {

    fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean
}
