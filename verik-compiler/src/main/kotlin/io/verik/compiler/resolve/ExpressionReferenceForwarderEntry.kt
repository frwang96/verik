/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.resolve

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.expression.common.EReceiverExpression

/**
 * Entry for forwarding the references of receiver expressions.
 */
data class ExpressionReferenceForwarderEntry(
    val receiverExpression: EReceiverExpression,
    val parentTypeArguments: List<Type>
)
