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
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.ast.property.KtUnaryOperatorKind
import io.verik.compiler.common.NullDeclaration
import io.verik.compiler.common.location
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.m
import org.jetbrains.kotlin.descriptors.PackageViewDescriptor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

class ExpressionCasterVisitor(private val castContext: CastContext) : KtVisitor<EElement, Unit>() {

    inline fun <reified T : EElement> getElement(element: KtElement): T? {
        return element.accept(this, Unit).cast()
    }

    fun getExpression(expression: KtExpression): EExpression {
        val location = expression.location()
        return getElement(expression) ?: ENullExpression(location)
    }

    override fun visitKtElement(element: KtElement, data: Unit?): EElement {
        m.error("Unrecognized element: $element", element)
        val location = element.location()
        return ENullElement(location)
    }

    override fun visitAnnotatedExpression(expression: KtAnnotatedExpression, data: Unit?): EElement {
        return getExpression(expression.baseExpression!!)
    }

    override fun visitParenthesizedExpression(expression: KtParenthesizedExpression, data: Unit?): EElement {
        return getExpression(expression.expression!!)
    }

    override fun visitBlockExpression(expression: KtBlockExpression, data: Unit?): EElement {
        val location = expression.location()
        val type = castContext.castType(expression)
        val statements = expression.statements.mapNotNull { getExpression(it) }
        return EKtBlockExpression(location, type, ArrayList(statements))
    }

    override fun visitPrefixExpression(expression: KtPrefixExpression, data: Unit?): EElement {
        val location = expression.location()
        val type = castContext.castType(expression)
        val kind = KtUnaryOperatorKind(expression.operationToken, location)
            ?: return ENullExpression(location)
        val childExpression = getExpression(expression.baseExpression!!)
        return EKtUnaryExpression(location, type, childExpression, kind)
    }

    override fun visitBinaryExpression(expression: KtBinaryExpression, data: Unit?): EElement {
        val location = expression.location()
        val type = castContext.castType(expression)
        val kind = KtBinaryOperatorKind(expression.operationToken, location)
            ?: return ENullExpression(location)
        val left = getExpression(expression.left!!)
        val right = getExpression(expression.right!!)
        return EKtBinaryExpression(location, type, left, right, kind)
    }

    override fun visitSimpleNameExpression(expression: KtSimpleNameExpression, data: Unit?): EElement {
        val location = expression.location()
        val descriptor = castContext.bindingContext.getSliceContents(BindingContext.REFERENCE_TARGET)[expression]!!
        val type = castContext.castType(expression)
        val declaration = castContext.getDeclaration(descriptor, expression)
        return ESimpleNameExpression(location, type, declaration, null)
    }

    override fun visitCallExpression(expression: KtCallExpression, data: Unit?): EElement {
        val location = expression.location()
        val descriptor = castContext.bindingContext
            .getSliceContents(BindingContext.REFERENCE_TARGET)[expression.calleeExpression]!!
        val type = castContext.castType(expression)
        val declaration = castContext.getDeclaration(descriptor, expression)
        val typeArguments = expression.typeArguments.map {
            getElement<ETypeArgument>(it) ?: return ENullExpression(location)
        }
        val valueArguments = expression.valueArguments.mapNotNull { getElement<EValueArgument>(it) }
        return ECallExpression(location, type, declaration, null, ArrayList(typeArguments), ArrayList(valueArguments))
    }

    override fun visitTypeProjection(typeProjection: KtTypeProjection, data: Unit?): EElement {
        val location = typeProjection.location()
        val type = castContext.castType(typeProjection.typeReference!!)
        return ETypeArgument(location, NullDeclaration, type)
    }

    override fun visitArgument(argument: KtValueArgument, data: Unit?): EElement {
        val location = argument.location()
        val expression = getExpression(argument.getArgumentExpression()!!)
        return if (argument.isNamed()) {
            val descriptor = castContext.bindingContext
                .getSliceContents(BindingContext.REFERENCE_TARGET)[argument.getArgumentName()!!.referenceExpression]!!
            val declaration = castContext.getDeclaration(descriptor, argument)
            EValueArgument(location, declaration, expression)
        } else {
            EValueArgument(location, NullDeclaration, expression)
        }
    }

    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression, data: Unit?): EElement {
        val location = expression.location()
        val type = castContext.castType(expression)

        // Drop receiver if reference target is a package
        val packageViewDescriptor = when (val receiver = expression.receiverExpression) {
            is KtSimpleNameExpression -> castContext.bindingContext
                .getSliceContents(BindingContext.REFERENCE_TARGET)[receiver]
            is KtDotQualifiedExpression -> castContext.bindingContext
                .getSliceContents(BindingContext.REFERENCE_TARGET)[receiver.selectorExpression]
            else -> null
        }
        val receiver = if (packageViewDescriptor is PackageViewDescriptor) null
        else getExpression(expression.receiverExpression)

        return when (val selector = expression.selectorExpression) {
            is KtSimpleNameExpression -> {
                val descriptor = castContext.bindingContext
                    .getSliceContents(BindingContext.REFERENCE_TARGET)[selector]!!
                val declaration = castContext.getDeclaration(descriptor, expression)
                ESimpleNameExpression(location, type, declaration, receiver)
            }
            is KtCallExpression -> {
                val descriptor = castContext.bindingContext
                    .getSliceContents(BindingContext.REFERENCE_TARGET)[selector.calleeExpression]!!
                val declaration = castContext.getDeclaration(descriptor, expression)
                val typeArguments = selector.typeArguments.map {
                    getElement<ETypeArgument>(it) ?: return ENullExpression(location)
                }
                val valueArguments = selector.valueArguments.mapNotNull { getElement<EValueArgument>(it) }
                return ECallExpression(
                    location,
                    type,
                    declaration,
                    receiver,
                    ArrayList(typeArguments),
                    ArrayList(valueArguments)
                )
            }
            else -> {
                m.error("Simple name expression or call expression expected", expression)
                ENullExpression(location)
            }
        }
    }

    override fun visitConstantExpression(expression: KtConstantExpression, data: Unit?): EElement {
        val location = expression.location()
        val type = castContext.castType(expression)
        val value = ConstantExpressionCaster.cast(expression.text, type, location)
        return EConstantExpression(location, type, value)
    }

    override fun visitLambdaExpression(expression: KtLambdaExpression, data: Unit?): EElement {
        val location = expression.location()
        val statements = expression.bodyExpression!!.statements.mapNotNull { getExpression(it) }
        val body = EKtBlockExpression(
            location,
            Core.Kt.FUNCTION.toType(),
            ArrayList(statements)
        )
        return EFunctionLiteralExpression(location, body)
    }

    override fun visitStringTemplateExpression(expression: KtStringTemplateExpression, data: Unit?): EElement {
        val location = expression.location()
        val entries = expression.entries.mapNotNull { getElement<EStringTemplateEntry>(it) }
        return EStringTemplateExpression(location, entries)
    }

    override fun visitLiteralStringTemplateEntry(entry: KtLiteralStringTemplateEntry, data: Unit?): EElement {
        val location = entry.location()
        val text = entry.text
        return ELiteralStringTemplateEntry(location, text)
    }

    override fun visitEscapeStringTemplateEntry(entry: KtEscapeStringTemplateEntry, data: Unit?): EElement {
        val location = entry.location()
        val text = entry.unescapedValue
        return ELiteralStringTemplateEntry(location, text)
    }

    override fun visitStringTemplateEntryWithExpression(
        entry: KtStringTemplateEntryWithExpression,
        data: Unit?
    ): EElement {
        val location = entry.location()
        val expression = getExpression(entry.expression!!)
        return EExpressionStringTemplateEntry(location, expression)
    }

    override fun visitIfExpression(expression: KtIfExpression, data: Unit?): EElement {
        val location = expression.location()
        val type = castContext.castType(expression)
        val condition = getExpression(expression.condition!!)
        val thenExpression = expression.then?.let { getExpression(it) }
        val elseExpression = expression.`else`?.let { getExpression(it) }
        return EIfExpression(location, type, condition, thenExpression, elseExpression)
    }
}