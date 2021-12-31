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
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.common.EReturnStatement
import io.verik.compiler.ast.element.common.ESuperExpression
import io.verik.compiler.ast.element.common.EThisExpression
import io.verik.compiler.ast.element.common.EWhileStatement
import io.verik.compiler.ast.element.kt.EAsExpression
import io.verik.compiler.ast.element.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.kt.EIsExpression
import io.verik.compiler.ast.element.kt.EKtArrayAccessExpression
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtForStatement
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.element.kt.EKtUnaryExpression
import io.verik.compiler.ast.element.kt.EKtValueParameter
import io.verik.compiler.ast.interfaces.cast
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.ast.property.KtUnaryOperatorKind
import io.verik.compiler.common.endLocation
import io.verik.compiler.common.location
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.descriptors.PackageViewDescriptor
import org.jetbrains.kotlin.psi.KtArrayAccessExpression
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBinaryExpressionWithTypeRHS
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtContainerNodeForControlStructureBody
import org.jetbrains.kotlin.psi.KtDoWhileExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtIsExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtPostfixExpression
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtSuperExpression
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.KtWhileExpression
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyClassDescriptor

object ExpressionCaster {

    fun castKtBlockExpression(expression: KtBlockExpression, castContext: CastContext): EKtBlockExpression {
        val location = expression.location()
        val endLocation = expression.endLocation()
        val type = if (expression.parent is KtContainerNodeForControlStructureBody &&
            expression.parent.parent is KtDoWhileExpression
        ) {
            Core.Kt.C_Unit.toType()
        } else {
            castContext.castType(expression)
        }
        val statements = expression.statements.mapNotNull { castContext.casterVisitor.getExpression(it) }
        return EKtBlockExpression(location, endLocation, type, ArrayList(statements))
    }

    fun castKtUnaryExpressionPrefix(expression: KtPrefixExpression, castContext: CastContext): EKtUnaryExpression {
        val location = expression.location()
        val type = castContext.castType(expression)
        val kind = KtUnaryOperatorKind.getKindPrefix(expression.operationToken, location)
        val childExpression = castContext.casterVisitor.getExpression(expression.baseExpression!!)
        return EKtUnaryExpression(location, type, childExpression, kind)
    }

    fun castKtUnaryExpressionPostfix(expression: KtPostfixExpression, castContext: CastContext): EKtUnaryExpression {
        val location = expression.location()
        val type = castContext.castType(expression)
        val kind = KtUnaryOperatorKind.getKindPostfix(expression.operationToken, location)
        val childExpression = castContext.casterVisitor.getExpression(expression.baseExpression!!)
        return EKtUnaryExpression(location, type, childExpression, kind)
    }

    fun castKtBinaryExpressionOrKtCallExpression(
        expression: KtBinaryExpression,
        castContext: CastContext
    ): EExpression {
        val location = expression.location()
        val type = castContext.castType(expression)
        val left = castContext.casterVisitor.getExpression(expression.left!!)
        val right = castContext.casterVisitor.getExpression(expression.right!!)
        val token = expression.operationReference.operationSignTokenType
        return if (token != null) {
            val kind = KtBinaryOperatorKind(token, location)
            EKtBinaryExpression(location, type, left, right, kind)
        } else {
            val descriptor = castContext.sliceReferenceTarget[expression.operationReference]!!
            val declaration = castContext.getDeclaration(descriptor, expression)
            EKtCallExpression(location, type, declaration, left, arrayListOf(right), arrayListOf())
        }
    }

    fun castReferenceExpression(
        expression: KtSimpleNameExpression,
        castContext: CastContext
    ): EReferenceExpression {
        val location = expression.location()
        val descriptor = castContext.sliceReferenceTarget[expression]!!
        val type = castContext.castType(expression)
        val declaration = castContext.getDeclaration(descriptor, expression)
        val referenceExpression = EReferenceExpression(location, type, declaration, null)
        ReferenceExpressionCaster.checkSmartCast(expression, referenceExpression, castContext)
        return referenceExpression
    }

    fun castKtCallExpression(expression: KtCallExpression, castContext: CastContext): EKtCallExpression {
        val location = expression.location()
        val descriptor = castContext.sliceReferenceTarget[expression.calleeExpression]!!
        val type = castContext.castType(expression)
        val declaration = castContext.getDeclaration(descriptor, expression)
        val valueArguments = CallExpressionCaster.castValueArguments(expression.calleeExpression!!, castContext)
        val typeArguments = CallExpressionCaster.castTypeArguments(expression, castContext)
        return EKtCallExpression(location, type, declaration, null, valueArguments, typeArguments)
    }

    fun castKtReferenceExpressionOrKtCallExpression(
        expression: KtDotQualifiedExpression,
        castContext: CastContext
    ): EExpression {
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
                val referenceExpression = EReferenceExpression(location, type, declaration, receiver)
                ReferenceExpressionCaster.checkSmartCast(expression, referenceExpression, castContext)
                referenceExpression
            }
            is KtCallExpression -> {
                val descriptor = castContext.sliceReferenceTarget[selector.calleeExpression]!!
                val declaration = castContext.getDeclaration(descriptor, expression)
                val valueArguments = CallExpressionCaster.castValueArguments(selector.calleeExpression!!, castContext)
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
            else -> Messages.INTERNAL_ERROR.on(expression, "Simple name expression or call expression expected")
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

    fun castSuperExpression(expression: KtSuperExpression, castContext: CastContext): ESuperExpression {
        val location = expression.location()
        val type = castContext.castType(expression)
        return ESuperExpression(location, type)
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
    ): EFunctionLiteralExpression {
        val location = expression.location()
        val endLocation = expression.endLocation()
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
                valueParameter.type = castContext.castType(parameterDescriptor.type, expression)
                listOf(valueParameter)
            } else listOf()
        }

        val statements = expression.bodyExpression!!.statements.map {
            castContext.casterVisitor.getExpression(it)
        }
        val body = EKtBlockExpression(
            location,
            endLocation,
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

    fun castIsExpression(expression: KtIsExpression, castContext: CastContext): EIsExpression {
        val location = expression.location()
        val childExpression = castContext.casterVisitor.getExpression(expression.leftHandSide)
        val castType = castContext.castType(expression.typeReference!!)
        val property = EKtProperty(location, location, "<tmp>")
        property.init(castType, null, listOf(), false)
        return EIsExpression(
            location,
            childExpression,
            property,
            expression.isNegated,
            castType
        )
    }

    fun castAsExpression(expression: KtBinaryExpressionWithTypeRHS, castContext: CastContext): EAsExpression {
        val location = expression.location()
        val type = castContext.castType(expression.right!!)
        val childExpression = castContext.casterVisitor.getExpression(expression.left)
        return EAsExpression(location, type, childExpression)
    }

    fun castIfExpression(expression: KtIfExpression, castContext: CastContext): EIfExpression {
        val location = expression.location()
        val type = castContext.castType(expression)
        val condition = castContext.casterVisitor.getExpression(expression.condition!!)
        val thenExpression = expression.then?.let {
            EKtBlockExpression.wrap(castContext.casterVisitor.getExpression(it))
        }
        val elseExpression = expression.`else`?.let {
            EKtBlockExpression.wrap(castContext.casterVisitor.getExpression(it))
        }
        return EIfExpression(location, type, condition, thenExpression, elseExpression)
    }

    fun castWhileStatement(expression: KtWhileExpression, castContext: CastContext): EWhileStatement {
        val location = expression.location()
        val condition = castContext.casterVisitor.getExpression(expression.condition!!)
        val body = castContext.casterVisitor.getExpression(expression.body!!)
        return EWhileStatement(location, condition, body, false)
    }

    fun castDoWhileStatement(expression: KtDoWhileExpression, castContext: CastContext): EWhileStatement {
        val location = expression.location()
        val condition = castContext.casterVisitor.getExpression(expression.condition!!)
        val body = castContext.casterVisitor.getExpression(expression.body!!)
        return EWhileStatement(location, condition, body, true)
    }

    fun castKtForStatement(expression: KtForExpression, castContext: CastContext): EKtForStatement? {
        val location = expression.location()
        val valueParameter = castContext.casterVisitor
            .getElement<EKtValueParameter>(expression.loopParameter!!)
            ?: return null
        val range = castContext.casterVisitor.getExpression(expression.loopRange!!)
        val bodyExpression = castContext.casterVisitor.getExpression(expression.body!!)
        val body = if (bodyExpression is EKtBlockExpression) {
            bodyExpression
        } else {
            EKtBlockExpression(
                bodyExpression.location,
                bodyExpression.location,
                Core.Kt.C_Unit.toType(),
                arrayListOf(bodyExpression)
            )
        }
        return EKtForStatement(location, valueParameter, range, body)
    }
}
