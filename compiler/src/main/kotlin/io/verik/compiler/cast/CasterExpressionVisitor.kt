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

import io.verik.compiler.ast.element.common.*
import io.verik.compiler.ast.element.kt.*
import io.verik.compiler.ast.property.KtOperatorKind
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.NullDeclaration
import io.verik.compiler.common.getSourceLocation
import io.verik.compiler.core.CoreClass
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getType

class CasterExpressionVisitor(
    projectContext: ProjectContext,
    private val declarationMap: DeclarationMap
) : KtVisitor<CElement, Unit>() {

    private val bindingContext = projectContext.bindingContext

    inline fun <reified T : CElement> getElement(element: KtElement): T? {
        return element.accept(this, Unit).cast(element)
    }

    private fun getType(expression: KtExpression): Type {
        return TypeCaster.castFromType(declarationMap, expression.getType(bindingContext)!!, expression)
    }

    override fun visitKtElement(element: KtElement, data: Unit?): CElement? {
        m.error("Unrecognized element: ${element::class.simpleName}", element)
        return null
    }

    override fun visitBlockExpression(expression: KtBlockExpression, data: Unit?): CElement {
        val location = expression.getSourceLocation()
        val type = getType(expression)
        val statements = expression.statements.mapNotNull { getElement<CExpression>(it) }
        return CBlockExpression(location, type, ArrayList(statements))
    }

    override fun visitParenthesizedExpression(expression: KtParenthesizedExpression, data: Unit?): CElement? {
        val location = expression.getSourceLocation()
        val type = getType(expression)
        val childExpression = getElement<CExpression>(expression.expression!!)
            ?: return null
        return CParenthesizedExpression(location, type, childExpression)
    }

    override fun visitBinaryExpression(expression: KtBinaryExpression, data: Unit?): CElement? {
        val location = expression.getSourceLocation()
        val type = getType(expression)
        val kind = KtOperatorKind(expression.operationToken, location)
            ?: return null
        val left = getElement<CExpression>(expression.left!!)
            ?: return null
        val right = getElement<CExpression>(expression.right!!)
            ?: return null
        return KBinaryExpression(location, type, left, right, kind)
    }

    override fun visitReferenceExpression(expression: KtReferenceExpression, data: Unit?): CElement {
        val descriptor = bindingContext.getSliceContents(BindingContext.REFERENCE_TARGET)[expression]!!
        val location = expression.getSourceLocation()
        val type = getType(expression)
        val declaration = declarationMap[descriptor, expression]
        return CReferenceExpression(location, type, declaration)
    }

    override fun visitCallExpression(expression: KtCallExpression, data: Unit?): CElement {
        val descriptor = bindingContext
            .getSliceContents(BindingContext.REFERENCE_TARGET)[expression.calleeExpression]!!
        val location = expression.getSourceLocation()
        val type = getType(expression)
        val declaration = declarationMap[descriptor, expression]
        val valueArguments = expression.valueArguments.mapNotNull { getElement<CValueArgument>(it) }
        return CCallExpression(location, type, declaration, ArrayList(valueArguments))
    }

    override fun visitArgument(argument: KtValueArgument, data: Unit?): CElement? {
        val location = argument.getSourceLocation()
        val expression = getElement<CExpression>(argument.getArgumentExpression()!!)
            ?: return null
        return if (argument.isNamed()) {
            val descriptor = bindingContext
                .getSliceContents(BindingContext.REFERENCE_TARGET)[argument.getArgumentName()!!.referenceExpression]!!
            val declaration = declarationMap[descriptor, argument]
            CValueArgument(location, declaration, expression)
        } else {
            CValueArgument(location, NullDeclaration, expression)
        }
    }

    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression, data: Unit?): CElement? {
        val location = expression.getSourceLocation()
        val type = getType(expression)
        val receiver = getElement<CExpression>(expression.receiverExpression)
            ?: return null
        val selector = getElement<CExpression>(expression.selectorExpression!!)
            ?: return null
        return CDotQualifiedExpression(location, type, receiver, selector)
    }

    override fun visitConstantExpression(expression: KtConstantExpression, data: Unit?): CElement {
        val location = expression.getSourceLocation()
        val type = getType(expression)
        val value = ConstantExpressionCaster.cast(expression.text, type, location)
        return CConstantExpression(location, type, value)
    }

    override fun visitLambdaExpression(expression: KtLambdaExpression, data: Unit?): CElement {
        val location = expression.getSourceLocation()
        val statements = expression.bodyExpression!!.statements.mapNotNull { getElement<CExpression>(it) }
        val bodyBlockExpression = CBlockExpression(
            location,
            CoreClass.Kotlin.FUNCTION.toNoArgumentsType(),
            ArrayList(statements)
        )
        return KFunctionLiteralExpression(location, bodyBlockExpression)
    }

    override fun visitStringTemplateExpression(expression: KtStringTemplateExpression, data: Unit?): CElement {
        val location = expression.getSourceLocation()
        val entries = expression.entries.mapNotNull { getElement<KStringTemplateEntry>(it) }
        return KStringTemplateExpression(location, entries)
    }

    override fun visitLiteralStringTemplateEntry(entry: KtLiteralStringTemplateEntry, data: Unit?): CElement {
        val location = entry.getSourceLocation()
        val text = entry.text
        return KLiteralStringTemplateEntry(location, text)
    }

    override fun visitEscapeStringTemplateEntry(entry: KtEscapeStringTemplateEntry, data: Unit?): CElement {
        val location = entry.getSourceLocation()
        val text = entry.unescapedValue
        return KLiteralStringTemplateEntry(location, text)
    }

    override fun visitStringTemplateEntryWithExpression(
        entry: KtStringTemplateEntryWithExpression,
        data: Unit?
    ): CElement? {
        val location = entry.getSourceLocation()
        val expression = getElement<CExpression>(entry.expression!!)
            ?: return null
        return KExpressionStringTemplateEntry(location, expression)
    }
}