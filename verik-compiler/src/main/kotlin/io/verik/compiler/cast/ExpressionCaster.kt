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

import io.verik.compiler.ast.element.common.EBlockExpression
import io.verik.compiler.ast.element.common.ECallExpression
import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EIfExpression
import io.verik.compiler.ast.element.common.ENullExpression
import io.verik.compiler.ast.element.common.EProperty
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
import io.verik.compiler.ast.element.kt.EKtForStatement
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

    fun castKtBlockExpression(expression: KtBlockExpression, castContext: CastContext): EBlockExpression {
        val location = expression.location()
        val endLocation = expression.endLocation()
        val type = if (expression.parent is KtContainerNodeForControlStructureBody &&
            expression.parent.parent is KtDoWhileExpression
        ) {
            Core.Kt.C_Unit.toType()
        } else {
            castContext.castType(expression)
        }
        val statements = expression.statements.mapNotNull { castContext.castExpression(it) }
        return EBlockExpression(location, endLocation, type, ArrayList(statements))
    }

    fun castKtUnaryExpressionPrefix(expression: KtPrefixExpression, castContext: CastContext): EKtUnaryExpression {
        val location = expression.location()
        val type = castContext.castType(expression)
        val kind = KtUnaryOperatorKind.getKindPrefix(expression.operationToken, location)
        val childExpression = castContext.castExpression(expression.baseExpression!!)
        return EKtUnaryExpression(location, type, childExpression, kind)
    }

    fun castKtUnaryExpressionPostfix(expression: KtPostfixExpression, castContext: CastContext): EKtUnaryExpression {
        val location = expression.location()
        val type = castContext.castType(expression)
        val kind = KtUnaryOperatorKind.getKindPostfix(expression.operationToken, location)
        val childExpression = castContext.castExpression(expression.baseExpression!!)
        return EKtUnaryExpression(location, type, childExpression, kind)
    }

    fun castKtBinaryExpressionOrCallExpression(
        expression: KtBinaryExpression,
        castContext: CastContext
    ): EExpression {
        val location = expression.location()
        val type = castContext.castType(expression)
        val left = castContext.castExpression(expression.left!!)
        val right = castContext.castExpression(expression.right!!)
        val token = expression.operationReference.operationSignTokenType
        return if (token != null) {
            val kind = KtBinaryOperatorKind(token, location)
            EKtBinaryExpression(location, type, left, right, kind)
        } else {
            val descriptor = castContext.sliceReferenceTarget[expression.operationReference]!!
            val declaration = castContext.resolveDeclaration(descriptor, expression)
            ECallExpression(location, type, declaration, left, arrayListOf(right), arrayListOf())
        }
    }

    fun castReferenceExpression(
        expression: KtSimpleNameExpression,
        castContext: CastContext
    ): EReferenceExpression {
        val location = expression.location()
        val descriptor = castContext.sliceReferenceTarget[expression]!!
        val type = castContext.castType(expression)
        val declaration = castContext.resolveDeclaration(descriptor, expression)
        val referenceExpression = EReferenceExpression(location, type, declaration, null)
        ReferenceExpressionCaster.checkSmartCast(expression, referenceExpression, castContext)
        return referenceExpression
    }

    fun castCallExpression(expression: KtCallExpression, castContext: CastContext): ECallExpression {
        val location = expression.location()
        val descriptor = castContext.sliceReferenceTarget[expression.calleeExpression]!!
        val type = castContext.castType(expression)
        val declaration = castContext.resolveDeclaration(descriptor, expression)
        val valueArguments = CallExpressionCaster.castValueArguments(expression.calleeExpression!!, castContext)
        val typeArguments = CallExpressionCaster.castTypeArguments(expression, castContext)
        return ECallExpression(location, type, declaration, null, valueArguments, typeArguments)
    }

    fun castReferenceExpressionOrCallExpression(
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
            else -> castContext.castExpression(expression.receiverExpression)
        }

        return when (val selector = expression.selectorExpression) {
            is KtSimpleNameExpression -> {
                val descriptor = castContext.sliceReferenceTarget[selector]!!
                val declaration = castContext.resolveDeclaration(descriptor, expression)
                val referenceExpression = EReferenceExpression(location, type, declaration, receiver)
                ReferenceExpressionCaster.checkSmartCast(expression, referenceExpression, castContext)
                referenceExpression
            }
            is KtCallExpression -> {
                val descriptor = castContext.sliceReferenceTarget[selector.calleeExpression]!!
                val declaration = castContext.resolveDeclaration(descriptor, expression)
                val valueArguments = CallExpressionCaster.castValueArguments(selector.calleeExpression!!, castContext)
                val typeArguments = CallExpressionCaster.castTypeArguments(selector, castContext)
                return ECallExpression(
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

    fun castConstantExpression(expression: KtConstantExpression, castContext: CastContext): EExpression {
        val location = expression.location()
        val type = castContext.castType(expression)
        val value = expression.text
        return if (value != "null") {
            EConstantExpression(location, type, value)
        } else {
            ENullExpression(location)
        }
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
        val returnedExpression = expression.returnedExpression?.let { castContext.castExpression(it) }
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
                castContext.castValueParameter(it)
            }
        } else {
            val functionDescriptor = castContext.sliceFunction[expression.functionLiteral]!!
            if (functionDescriptor.valueParameters.isNotEmpty()) {
                val parameterDescriptor = functionDescriptor.valueParameters[0]
                val valueParameter = castContext.resolveDeclaration(parameterDescriptor, expression)
                    .cast<EKtValueParameter>(expression)
                valueParameter.type = castContext.castType(parameterDescriptor.type, expression)
                listOf(valueParameter)
            } else listOf()
        }

        val statements = expression.bodyExpression!!.statements.map {
            castContext.castExpression(it)
        }
        val bodyType = castContext.castType(expression)
        val body = EBlockExpression(
            location,
            endLocation,
            bodyType,
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
        val array = castContext.castExpression(expression.arrayExpression!!)
        val indices = expression.indexExpressions.map { castContext.castExpression(it) }
        return EKtArrayAccessExpression(location, type, array, ArrayList(indices))
    }

    fun castIsExpression(expression: KtIsExpression, castContext: CastContext): EIsExpression {
        val location = expression.location()
        val childExpression = castContext.castExpression(expression.leftHandSide)
        val castType = castContext.castType(expression.typeReference!!)
        val property = EProperty.temporary(
            location = location,
            type = castType,
            initializer = null,
            isMutable = false
        )
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
        val childExpression = castContext.castExpression(expression.left)
        return EAsExpression(location, type, childExpression)
    }

    fun castIfExpression(expression: KtIfExpression, castContext: CastContext): EIfExpression {
        val location = expression.location()
        val type = castContext.castType(expression)
        val condition = castContext.castExpression(expression.condition!!)
        val thenExpression = expression.then?.let {
            EBlockExpression.wrap(castContext.castExpression(it))
        }
        val elseExpression = expression.`else`?.let {
            EBlockExpression.wrap(castContext.castExpression(it))
        }
        return EIfExpression(location, type, condition, thenExpression, elseExpression)
    }

    fun castWhileStatement(expression: KtWhileExpression, castContext: CastContext): EWhileStatement {
        val location = expression.location()
        val condition = castContext.castExpression(expression.condition!!)
        val body = castContext.castExpression(expression.body!!)
        return EWhileStatement(
            location,
            condition,
            EBlockExpression.wrap(body),
            false
        )
    }

    fun castDoWhileStatement(expression: KtDoWhileExpression, castContext: CastContext): EWhileStatement {
        val location = expression.location()
        val condition = castContext.castExpression(expression.condition!!)
        val body = castContext.castExpression(expression.body!!)
        return EWhileStatement(
            location,
            condition,
            EBlockExpression.wrap(body),
            true
        )
    }

    fun castKtForStatement(expression: KtForExpression, castContext: CastContext): EKtForStatement? {
        val location = expression.location()
        val valueParameter = castContext.castValueParameter(expression.loopParameter!!)
            ?: return null
        val range = castContext.castExpression(expression.loopRange!!)
        val bodyExpression = castContext.castExpression(expression.body!!)
        val body = if (bodyExpression is EBlockExpression) {
            bodyExpression
        } else {
            EBlockExpression(
                bodyExpression.location,
                bodyExpression.location,
                Core.Kt.C_Unit.toType(),
                arrayListOf(bodyExpression)
            )
        }
        return EKtForStatement(location, valueParameter, range, body)
    }
}
