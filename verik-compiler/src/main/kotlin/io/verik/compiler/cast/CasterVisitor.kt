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
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EIfExpression
import io.verik.compiler.ast.element.common.ENullExpression
import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.ast.element.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtEnumEntry
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.element.kt.EKtReferenceExpression
import io.verik.compiler.ast.element.kt.EKtUnaryExpression
import io.verik.compiler.ast.element.kt.EStringTemplateExpression
import io.verik.compiler.common.location
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.psi.KtAnnotatedExpression
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtEnumEntry
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParenthesizedExpression
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.KtVisitor

class CasterVisitor(private val castContext: CastContext) : KtVisitor<EElement, Unit>() {

    inline fun <reified T : EElement> getElement(element: KtElement): T? {
        @Suppress("UNNECESSARY_SAFE_CALL")
        return element.accept(this, Unit)?.cast()
    }

    fun getExpression(expression: KtExpression): EExpression {
        val location = expression.location()
        return getElement(expression) ?: ENullExpression(location)
    }

    override fun visitKtElement(element: KtElement, data: Unit?): EElement? {
        Messages.INTERNAL_ERROR.on(element, "Unrecognized element: $element")
        return null
    }

    override fun visitClassOrObject(classOrObject: KtClassOrObject, data: Unit?): EKtBasicClass? {
        return DeclarationCaster.castKtBasicClass(classOrObject, castContext)
    }

    override fun visitEnumEntry(enumEntry: KtEnumEntry, data: Unit?): EKtEnumEntry? {
        return DeclarationCaster.castKtEnumEntry(enumEntry, castContext)
    }

    override fun visitNamedFunction(function: KtNamedFunction, data: Unit?): EKtFunction? {
        return DeclarationCaster.castKtFunction(function, castContext)
    }

    override fun visitProperty(property: KtProperty, data: Unit?): EKtProperty? {
        return DeclarationCaster.castKtProperty(property, castContext)
    }

    override fun visitTypeParameter(parameter: KtTypeParameter, data: Unit?): ETypeParameter? {
        return DeclarationCaster.castTypeParameter(parameter, castContext)
    }

    override fun visitAnnotatedExpression(expression: KtAnnotatedExpression, data: Unit?): EExpression {
        return getExpression(expression.baseExpression!!)
    }

    override fun visitParenthesizedExpression(expression: KtParenthesizedExpression, data: Unit?): EExpression {
        return getExpression(expression.expression!!)
    }

    override fun visitBlockExpression(expression: KtBlockExpression, data: Unit?): EKtBlockExpression {
        return ExpressionCaster.castKtBlockExpression(expression, castContext)
    }

    override fun visitPrefixExpression(expression: KtPrefixExpression, data: Unit?): EKtUnaryExpression? {
        return ExpressionCaster.castKtUnaryExpression(expression, castContext)
    }

    override fun visitBinaryExpression(expression: KtBinaryExpression, data: Unit?): EKtBinaryExpression? {
        return ExpressionCaster.castKtBinaryExpression(expression, castContext)
    }

    override fun visitSimpleNameExpression(expression: KtSimpleNameExpression, data: Unit?): EKtReferenceExpression {
        return ExpressionCaster.castKtReferenceExpression(expression, castContext)
    }

    override fun visitCallExpression(expression: KtCallExpression, data: Unit?): EKtCallExpression {
        return ExpressionCaster.castKtCallExpression(expression, castContext)
    }

    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression, data: Unit?): EExpression? {
        return ExpressionCaster.castKtReferenceExpressionOrKtCallExpression(expression, castContext)
    }

    override fun visitConstantExpression(expression: KtConstantExpression, data: Unit?): EConstantExpression {
        return ExpressionCaster.castConstantExpression(expression, castContext)
    }

    override fun visitLambdaExpression(expression: KtLambdaExpression, data: Unit?): EFunctionLiteralExpression {
        return ExpressionCaster.castFunctionLiteralExpression(expression, castContext)
    }

    override fun visitStringTemplateExpression(
        expression: KtStringTemplateExpression,
        data: Unit?
    ): EStringTemplateExpression {
        return StringTemplateExpressionCaster.castStringTemplateExpression(expression, castContext)
    }

    override fun visitIfExpression(expression: KtIfExpression, data: Unit?): EIfExpression {
        return ExpressionCaster.castIfExpression(expression, castContext)
    }
}
