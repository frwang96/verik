/*
 * Copyright (c) 2021 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.verik.compiler.cast

import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EIfExpression
import io.verik.compiler.ast.element.common.EReturnStatement
import io.verik.compiler.ast.element.common.EThisExpression
import io.verik.compiler.ast.element.common.EWhileExpression
import io.verik.compiler.ast.element.kt.EForExpression
import io.verik.compiler.ast.element.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.kt.EKtArrayAccessExpression
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtReferenceExpression
import io.verik.compiler.ast.element.kt.EKtUnaryExpression
import io.verik.compiler.ast.element.kt.EKtValueParameter
import io.verik.compiler.ast.interfaces.cast
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.ast.property.KtUnaryOperatorKind
import io.verik.compiler.common.location
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.descriptors.PackageViewDescriptor
import org.jetbrains.kotlin.psi.KtArrayAccessExpression
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtContainerNodeForControlStructureBody
import org.jetbrains.kotlin.psi.KtDoWhileExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtPostfixExpression
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.KtWhileExpression
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyClassDescriptor

object ExpressionCaster {

    fun castKtBlockExpression(expression: KtBlockExpression, castContext: CastContext): EKtBlockExpression {
        val location = expression.location()
        val type = if (expression.parent is KtContainerNodeForControlStructureBody &&
            expression.parent.parent is KtDoWhileExpression
        ) {
            Core.Kt.C_Unit.toType()
        } else {
            castContext.castType(expression)
        }
        val statements = expression.statements.mapNotNull { castContext.casterVisitor.getExpression(it) }
        return EKtBlockExpression(location, type, ArrayList(statements))
    }

    fun castKtUnaryExpressionPrefix(expression: KtPrefixExpression, castContext: CastContext): EKtUnaryExpression? {
        val location = expression.location()
        val type = castContext.castType(expression)
        val kind = KtUnaryOperatorKind.getKindPrefix(expression.operationToken, location)
            ?: return null
        val childExpression = castContext.casterVisitor.getExpression(expression.baseExpression!!)
        return EKtUnaryExpression(location, type, childExpression, kind)
    }

    fun castKtUnaryExpressionPostfix(expression: KtPostfixExpression, castContext: CastContext): EKtUnaryExpression? {
        val location = expression.location()
        val type = castContext.castType(expression)
        val kind = KtUnaryOperatorKind.getKindPostfix(expression.operationToken, location)
            ?: return null
        val childExpression = castContext.casterVisitor.getExpression(expression.baseExpression!!)
        return EKtUnaryExpression(location, type, childExpression, kind)
    }

    fun castKtBinaryExpressionOrKtCallExpression(
        expression: KtBinaryExpression,
        castContext: CastContext
    ): EExpression? {
        val location = expression.location()
        val type = castContext.castType(expression)
        val left = castContext.casterVisitor.getExpression(expression.left!!)
        val right = castContext.casterVisitor.getExpression(expression.right!!)
        val token = expression.operationReference.operationSignTokenType
        return if (token != null) {
            val kind = KtBinaryOperatorKind(token, location)
                ?: return null
            EKtBinaryExpression(location, type, left, right, kind)
        } else {
            val descriptor = castContext.sliceReferenceTarget[expression.operationReference]!!
            val declaration = castContext.getDeclaration(descriptor, expression)
            EKtCallExpression(location, type, declaration, left, arrayListOf(right), arrayListOf())
        }
    }

    fun castKtReferenceExpression(
        expression: KtSimpleNameExpression,
        castContext: CastContext
    ): EKtReferenceExpression {
        val location = expression.location()
        val descriptor = castContext.sliceReferenceTarget[expression]!!
        val type = castContext.castType(expression)
        val declaration = castContext.getDeclaration(descriptor, expression)
        return EKtReferenceExpression(location, type, declaration, null)
    }

    fun castKtCallExpression(expression: KtCallExpression, castContext: CastContext): EKtCallExpression {
        val location = expression.location()
        val descriptor = castContext.sliceReferenceTarget[expression.calleeExpression]!!
        val type = castContext.castType(expression)
        val declaration = castContext.getDeclaration(descriptor, expression)
        val valueArguments = CallExpressionCaster.castValueArguments(expression, castContext)
        val typeArguments = CallExpressionCaster.castTypeArguments(expression, castContext)
        return EKtCallExpression(location, type, declaration, null, valueArguments, typeArguments)
    }

    fun castKtReferenceExpressionOrKtCallExpression(
        expression: KtDotQualifiedExpression,
        castContext: CastContext
    ): EExpression? {
        val location = expression.location()
        val type = castContext.castType(expression)

        // Drop receiver if reference target is a package or class
        val receiverReferenceTarget = when (val receiver = expression.receiverExpression) {
            is KtSimpleNameExpression -> castContext.sliceReferenceTarget[receiver]
            is KtDotQualifiedExpression -> castContext.sliceReferenceTarget[receiver.selectorExpression]
            else -> null
        }
        val receiver = when (receiverReferenceTarget) {
            is PackageViewDescriptor -> null
            is LazyClassDescriptor -> null
            else -> castContext.casterVisitor.getExpression(expression.receiverExpression)
        }

        return when (val selector = expression.selectorExpression) {
            is KtSimpleNameExpression -> {
                val descriptor = castContext.sliceReferenceTarget[selector]!!
                val declaration = castContext.getDeclaration(descriptor, expression)
                EKtReferenceExpression(location, type, declaration, receiver)
            }
            is KtCallExpression -> {
                val descriptor = castContext.sliceReferenceTarget[selector.calleeExpression]!!
                val declaration = castContext.getDeclaration(descriptor, expression)
                val valueArguments = CallExpressionCaster.castValueArguments(selector, castContext)
                val typeArguments = CallExpressionCaster.castTypeArguments(selector, castContext)
                return EKtCallExpression(
                    location,
                    type,
                    declaration,
                    receiver,
                    valueArguments,
                    typeArguments
                )
            }
            else -> {
                Messages.INTERNAL_ERROR.on(expression, "Simple name expression or call expression expected")
                null
            }
        }
    }

    fun castConstantExpression(expression: KtConstantExpression, castContext: CastContext): EConstantExpression {
        val location = expression.location()
        val type = castContext.castType(expression)
        val value = expression.text
        return EConstantExpression(location, type, value)
    }

    fun castThisExpression(expression: KtThisExpression, castContext: CastContext): EThisExpression {
        val location = expression.location()
        val type = castContext.castType(expression)
        return EThisExpression(location, type)
    }

    fun castReturnStatement(expression: KtReturnExpression, castContext: CastContext): EReturnStatement {
        val location = expression.location()
        val type = castContext.castType(expression)
        val returnedExpression = expression.returnedExpression?.let { castContext.casterVisitor.getExpression(it) }
        return EReturnStatement(location, type, returnedExpression)
    }

    fun castFunctionLiteralExpression(
        expression: KtLambdaExpression,
        castContext: CastContext
    ): EFunctionLiteralExpression? {
        val location = expression.location()
        val valueParameters = if (expression.functionLiteral.hasParameterSpecification()) {
            expression.valueParameters.mapNotNull {
                castContext.casterVisitor.getElement<EKtValueParameter>(it)
            }
        } else {
            val functionDescriptor = castContext.sliceFunction[expression.functionLiteral]!!
            if (functionDescriptor.valueParameters.isNotEmpty()) {
                val parameterDescriptor = functionDescriptor.valueParameters[0]
                val valueParameter = castContext.getDeclaration(parameterDescriptor, expression)
                    .cast<EKtValueParameter>(expression)
                    ?: return null
                valueParameter.type = castContext.castType(parameterDescriptor.type, expression)
                listOf(valueParameter)
            } else listOf()
        }

        val statements = expression.bodyExpression!!.statements.map {
            castContext.casterVisitor.getExpression(it)
        }
        val body = EKtBlockExpression(
            location,
            Core.Kt.C_Function.toType(),
            ArrayList(statements)
        )
        return EFunctionLiteralExpression(location, ArrayList(valueParameters), body)
    }

    fun castKtArrayAccessExpression(
        expression: KtArrayAccessExpression,
        castContext: CastContext
    ): EKtArrayAccessExpression {
        val location = expression.location()
        val parent = expression.parent
        val type = if (parent is KtBinaryExpression &&
            parent.operationReference.operationSignTokenType.toString() == "EQ"
        ) {
            castContext.castType(parent.right!!)
        } else {
            castContext.castType(expression)
        }
        val array = castContext.casterVisitor.getExpression(expression.arrayExpression!!)
        val indices = expression.indexExpressions.map { castContext.casterVisitor.getExpression(it) }
        return EKtArrayAccessExpression(location, type, array, ArrayList(indices))
    }

    fun castIfExpression(expression: KtIfExpression, castContext: CastContext): EIfExpression {
        val location = expression.location()
        val type = castContext.castType(expression)
        val condition = castContext.casterVisitor.getExpression(expression.condition!!)
        val thenExpression = expression.then?.let {
            castContext.casterVisitor.getExpression(it)
        }
        val elseExpression = expression.`else`?.let {
            castContext.casterVisitor.getExpression(it)
        }
        return EIfExpression(location, type, condition, thenExpression, elseExpression)
    }

    fun castWhileExpression(expression: KtWhileExpression, castContext: CastContext): EWhileExpression {
        val location = expression.location()
        val condition = castContext.casterVisitor.getExpression(expression.condition!!)
        val body = castContext.casterVisitor.getExpression(expression.body!!)
        return EWhileExpression(location, condition, body, false)
    }

    fun castDoWhileExpression(expression: KtDoWhileExpression, castContext: CastContext): EWhileExpression {
        val location = expression.location()
        val condition = castContext.casterVisitor.getExpression(expression.condition!!)
        val body = castContext.casterVisitor.getExpression(expression.body!!)
        return EWhileExpression(location, condition, body, true)
    }

    fun castForExpression(expression: KtForExpression, castContext: CastContext): EForExpression? {
        val location = expression.location()
        val valueParameter = castContext.casterVisitor
            .getElement<EKtValueParameter>(expression.loopParameter!!)
            ?: return null
        val range = castContext.casterVisitor.getExpression(expression.loopRange!!)
        val body = castContext.casterVisitor.getExpression(expression.body!!)
        return EForExpression(location, valueParameter, range, body)
    }
}
