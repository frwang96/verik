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

import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EIfExpression
import io.verik.compiler.ast.element.kt.*
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.ast.property.KtUnaryOperatorKind
import io.verik.compiler.common.location
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.m
import org.jetbrains.kotlin.descriptors.PackageViewDescriptor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyClassDescriptor

object ExpressionCaster {

    fun castKtBlockExpression(expression: KtBlockExpression, castContext: CastContext): EKtBlockExpression {
        val location = expression.location()
        val type = castContext.castType(expression)
        val statements = expression.statements.mapNotNull { castContext.casterVisitor.getExpression(it) }
        return EKtBlockExpression(location, type, ArrayList(statements))
    }

    fun castKtUnaryExpression(expression: KtPrefixExpression, castContext: CastContext): EKtUnaryExpression? {
        val location = expression.location()
        val type = castContext.castType(expression)
        val kind = KtUnaryOperatorKind(expression.operationToken, location)
            ?: return null
        val childExpression = castContext.casterVisitor.getExpression(expression.baseExpression!!)
        return EKtUnaryExpression(location, type, childExpression, kind)
    }

    fun castKtBinaryExpression(expression: KtBinaryExpression, castContext: CastContext): EKtBinaryExpression? {
        val location = expression.location()
        val type = castContext.castType(expression)
        val kind = KtBinaryOperatorKind(expression.operationToken, location)
            ?: return null
        val left = castContext.casterVisitor.getExpression(expression.left!!)
        val right = castContext.casterVisitor.getExpression(expression.right!!)
        return EKtBinaryExpression(location, type, left, right, kind)
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
                m.error("Simple name expression or call expression expected", expression)
                null
            }
        }
    }

    fun castFunctionLiteralExpression(
        expression: KtLambdaExpression,
        castContext: CastContext
    ): EFunctionLiteralExpression {
        val location = expression.location()
        val statements = expression.bodyExpression!!.statements.map {
            castContext.casterVisitor.getExpression(it)
        }
        val body = EKtBlockExpression(
            location,
            Core.Kt.FUNCTION.toType(),
            ArrayList(statements)
        )
        return EFunctionLiteralExpression(location, body)
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
}