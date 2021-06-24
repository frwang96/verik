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

import io.verik.compiler.ast.common.KtOperatorKind
import io.verik.compiler.ast.common.NullDeclaration
import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.*
import io.verik.compiler.common.getSourceLocation
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getType

class CasterExpressionVisitor(
    projectContext: ProjectContext,
    private val declarationMap: DeclarationMap
) : KtVisitor<VkElement, Unit>() {

    private val bindingContext = projectContext.bindingContext

    inline fun <reified T : VkElement> getElement(element: KtElement): T? {
        return element.accept(this, Unit).cast(element)
    }

    private fun getType(expression: KtExpression): Type {
        return TypeCaster.castType(declarationMap, expression.getType(bindingContext)!!, expression)
    }

    override fun visitKtElement(element: KtElement, data: Unit?): VkElement? {
        m.error("Unrecognized element: ${element::class.simpleName}", element)
        return null
    }

    override fun visitBlockExpression(expression: KtBlockExpression, data: Unit?): VkElement {
        val location = expression.getSourceLocation()
        val type = getType(expression)
        val statements = expression.statements.mapNotNull { getElement<VkExpression>(it) }
        return VkBlockExpression(location, type, ArrayList(statements))
    }

    override fun visitParenthesizedExpression(expression: KtParenthesizedExpression, data: Unit?): VkElement? {
        val location = expression.getSourceLocation()
        val type = getType(expression)
        val childExpression = getElement<VkExpression>(expression.expression!!)
            ?: return null
        return VkParenthesizedExpression(location, type, childExpression)
    }

    override fun visitBinaryExpression(expression: KtBinaryExpression, data: Unit?): VkElement? {
        val location = expression.getSourceLocation()
        val type = getType(expression)
        val kind = KtOperatorKind(expression.operationToken, location)
            ?: return null
        val left = getElement<VkExpression>(expression.left!!)
            ?: return null
        val right = getElement<VkExpression>(expression.right!!)
            ?: return null
        return VkKtBinaryExpression(location, type, left, right, kind)
    }

    override fun visitReferenceExpression(expression: KtReferenceExpression, data: Unit?): VkElement {
        val descriptor = bindingContext.getSliceContents(BindingContext.REFERENCE_TARGET)[expression]!!
        val location = expression.getSourceLocation()
        val type = getType(expression)
        val declaration = declarationMap[descriptor, expression]
        return VkReferenceExpression(location, type, declaration)
    }

    override fun visitCallExpression(expression: KtCallExpression, data: Unit?): VkElement {
        val descriptor = bindingContext
            .getSliceContents(BindingContext.REFERENCE_TARGET)[expression.calleeExpression]!!
        val location = expression.getSourceLocation()
        val type = getType(expression)
        val declaration = declarationMap[descriptor, expression]
        val valueArguments = expression.valueArguments.mapNotNull { getElement<VkValueArgument>(it) }
        return VkCallExpression(location, type, declaration, ArrayList(valueArguments))
    }

    override fun visitArgument(argument: KtValueArgument, data: Unit?): VkElement? {
        val location = argument.getSourceLocation()
        val expression = getElement<VkExpression>(argument.getArgumentExpression()!!)
            ?: return null
        return if (argument.isNamed()) {
            val descriptor = bindingContext
                .getSliceContents(BindingContext.REFERENCE_TARGET)[argument.getArgumentName()!!.referenceExpression]!!
            val declaration = declarationMap[descriptor, argument]
            VkValueArgument(location, declaration, expression)
        } else {
            VkValueArgument(location, NullDeclaration, expression)
        }
    }

    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression, data: Unit?): VkElement? {
        val location = expression.getSourceLocation()
        val type = getType(expression)
        val receiver = getElement<VkExpression>(expression.receiverExpression)
            ?: return null
        val selector = getElement<VkExpression>(expression.selectorExpression!!)
            ?: return null
        return VkDotQualifiedExpression(location, type, receiver, selector)
    }

    override fun visitConstantExpression(expression: KtConstantExpression, data: Unit?): VkElement {
        val location = expression.getSourceLocation()
        val type = getType(expression)
        val value = ConstantExpressionCaster.cast(expression.text, type, location)
        return VkConstantExpression(location, type, value)
    }
}