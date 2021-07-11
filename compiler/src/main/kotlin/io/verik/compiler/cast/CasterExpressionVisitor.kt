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
import io.verik.compiler.ast.property.KOperatorKind
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
) : KtVisitor<EElement, Unit>() {

    private val bindingContext = projectContext.bindingContext

    inline fun <reified T : EElement> getElement(element: KtElement): T? {
        return element.accept(this, Unit).cast(element)
    }

    private fun getType(expression: KtExpression): Type {
        return TypeCaster.castFromType(declarationMap, expression.getType(bindingContext)!!, expression)
    }

    override fun visitKtElement(element: KtElement, data: Unit?): EElement? {
        m.error("Unrecognized element: ${element::class.simpleName}", element)
        return null
    }

    override fun visitAnnotatedExpression(expression: KtAnnotatedExpression, data: Unit?): EElement? {
        return getElement<EExpression>(expression.baseExpression!!)
    }

    override fun visitBlockExpression(expression: KtBlockExpression, data: Unit?): EElement {
        val location = expression.getSourceLocation()
        val type = getType(expression)
        val statements = expression.statements.mapNotNull { getElement<EExpression>(it) }
        return EKtBlockExpression(location, type, ArrayList(statements))
    }

    override fun visitParenthesizedExpression(expression: KtParenthesizedExpression, data: Unit?): EElement? {
        val location = expression.getSourceLocation()
        val type = getType(expression)
        val childExpression = getElement<EExpression>(expression.expression!!)
            ?: return null
        return EParenthesizedExpression(location, type, childExpression)
    }

    override fun visitBinaryExpression(expression: KtBinaryExpression, data: Unit?): EElement? {
        val location = expression.getSourceLocation()
        val type = getType(expression)
        val kind = KOperatorKind(expression.operationToken, location)
            ?: return null
        val left = getElement<EExpression>(expression.left!!)
            ?: return null
        val right = getElement<EExpression>(expression.right!!)
            ?: return null
        return EKtBinaryExpression(location, type, left, right, kind)
    }

    override fun visitReferenceExpression(expression: KtReferenceExpression, data: Unit?): EElement {
        val descriptor = bindingContext.getSliceContents(BindingContext.REFERENCE_TARGET)[expression]!!
        val location = expression.getSourceLocation()
        val type = getType(expression)
        val declaration = declarationMap[descriptor, expression]
        return EReferenceExpression(location, type, declaration)
    }

    override fun visitCallExpression(expression: KtCallExpression, data: Unit?): EElement {
        val descriptor = bindingContext
            .getSliceContents(BindingContext.REFERENCE_TARGET)[expression.calleeExpression]!!
        val location = expression.getSourceLocation()
        val type = getType(expression)
        val declaration = declarationMap[descriptor, expression]
        val typeArguments = expression.typeArguments.map {
            val typeArgumentLocation = it.getSourceLocation()
            val typeArgumentType = TypeCaster.castFromTypeReference(bindingContext, declarationMap, it.typeReference!!)
            ETypeArgument(typeArgumentLocation, NullDeclaration, typeArgumentType)
        }
        val valueArguments = expression.valueArguments.mapNotNull { getElement<EValueArgument>(it) }
        return ECallExpression(location, type, declaration, ArrayList(typeArguments), ArrayList(valueArguments))
    }

    override fun visitArgument(argument: KtValueArgument, data: Unit?): EElement? {
        val location = argument.getSourceLocation()
        val expression = getElement<EExpression>(argument.getArgumentExpression()!!)
            ?: return null
        return if (argument.isNamed()) {
            val descriptor = bindingContext
                .getSliceContents(BindingContext.REFERENCE_TARGET)[argument.getArgumentName()!!.referenceExpression]!!
            val declaration = declarationMap[descriptor, argument]
            EValueArgument(location, declaration, expression)
        } else {
            EValueArgument(location, NullDeclaration, expression)
        }
    }

    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression, data: Unit?): EElement? {
        val location = expression.getSourceLocation()
        val type = getType(expression)
        val receiver = getElement<EExpression>(expression.receiverExpression)
            ?: return null
        val selector = getElement<EExpression>(expression.selectorExpression!!)
            ?: return null
        return EDotQualifiedExpression(location, type, receiver, selector)
    }

    override fun visitConstantExpression(expression: KtConstantExpression, data: Unit?): EElement {
        val location = expression.getSourceLocation()
        val type = getType(expression)
        val value = ConstantExpressionCaster.cast(expression.text, type, location)
        return EConstantExpression(location, type, value)
    }

    override fun visitLambdaExpression(expression: KtLambdaExpression, data: Unit?): EElement {
        val location = expression.getSourceLocation()
        val statements = expression.bodyExpression!!.statements.mapNotNull { getElement<EExpression>(it) }
        val bodyBlockExpression = EKtBlockExpression(
            location,
            CoreClass.Kotlin.FUNCTION.toNoArgumentsType(),
            ArrayList(statements)
        )
        return EFunctionLiteralExpression(location, bodyBlockExpression)
    }

    override fun visitStringTemplateExpression(expression: KtStringTemplateExpression, data: Unit?): EElement {
        val location = expression.getSourceLocation()
        val entries = expression.entries.mapNotNull { getElement<EStringTemplateEntry>(it) }
        return EStringTemplateExpression(location, entries)
    }

    override fun visitLiteralStringTemplateEntry(entry: KtLiteralStringTemplateEntry, data: Unit?): EElement {
        val location = entry.getSourceLocation()
        val text = entry.text
        return ELiteralStringTemplateEntry(location, text)
    }

    override fun visitEscapeStringTemplateEntry(entry: KtEscapeStringTemplateEntry, data: Unit?): EElement {
        val location = entry.getSourceLocation()
        val text = entry.unescapedValue
        return ELiteralStringTemplateEntry(location, text)
    }

    override fun visitStringTemplateEntryWithExpression(
        entry: KtStringTemplateEntryWithExpression,
        data: Unit?
    ): EElement? {
        val location = entry.getSourceLocation()
        val expression = getElement<EExpression>(entry.expression!!)
            ?: return null
        return EExpressionStringTemplateEntry(location, expression)
    }

    override fun visitIfExpression(expression: KtIfExpression, data: Unit?): EElement? {
        val location = expression.getSourceLocation()
        val type = getType(expression)
        val condition = getElement<EExpression>(expression.condition!!) ?: return null
        val thenExpression = expression.then?.let { getElement<EExpression>(it) ?: return null }
        val elseExpression = expression.`else`?.let { getElement<EExpression>(it) ?: return null }
        return EIfExpression(location, type, condition, thenExpression, elseExpression)
    }
}