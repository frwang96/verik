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

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.declaration.common.EEnumEntry
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.common.ETypeParameter
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.declaration.kt.EKtFunction
import io.verik.compiler.ast.element.declaration.kt.EKtValueParameter
import io.verik.compiler.ast.element.declaration.kt.EPrimaryConstructor
import io.verik.compiler.ast.element.declaration.kt.ETypeAlias
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EIfExpression
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.ast.element.expression.common.EReturnStatement
import io.verik.compiler.ast.element.expression.common.EWhileStatement
import io.verik.compiler.ast.element.expression.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.expression.kt.EKtArrayAccessExpression
import io.verik.compiler.ast.element.expression.kt.EKtForStatement
import io.verik.compiler.ast.element.expression.kt.EKtUnaryExpression
import io.verik.compiler.ast.element.expression.kt.EStringTemplateExpression
import io.verik.compiler.ast.element.expression.kt.EWhenExpression
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.psi.KtAnnotatedExpression
import org.jetbrains.kotlin.psi.KtArrayAccessExpression
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBinaryExpressionWithTypeRHS
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtDoWhileExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtEnumEntry
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtIsExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtParenthesizedExpression
import org.jetbrains.kotlin.psi.KtPostfixExpression
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtSuperExpression
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.KtTypeAlias
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.KtVisitor
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.psi.KtWhileExpression

class CasterVisitor(private val castContext: CastContext) : KtVisitor<EElement, Unit>() {

    inline fun <reified E : EElement> getElement(element: KtElement): E? {
        @Suppress("RedundantNullableReturnType")
        val castedElement: EElement? = element.accept(this, Unit)
        return if (castedElement != null) {
            return castedElement.cast()
        } else null
    }

    override fun visitKtElement(element: KtElement, data: Unit?): EElement? {
        Messages.INTERNAL_ERROR.on(element, "Unrecognized element: $element")
    }

    override fun visitTypeAlias(typeAlias: KtTypeAlias, data: Unit?): ETypeAlias {
        return DeclarationCaster.castTypeAlias(typeAlias, castContext)
    }

    override fun visitTypeParameter(parameter: KtTypeParameter, data: Unit?): ETypeParameter {
        return DeclarationCaster.castTypeParameter(parameter, castContext)
    }

    override fun visitClassOrObject(classOrObject: KtClassOrObject, data: Unit?): EKtClass {
        return DeclarationCaster.castClass(classOrObject, castContext)
    }

    override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor, data: Unit?): EPrimaryConstructor {
        return DeclarationCaster.castPrimaryConstructor(constructor, castContext)
    }

    override fun visitNamedFunction(function: KtNamedFunction, data: Unit?): EKtFunction {
        return DeclarationCaster.castFunction(function, castContext)
    }

    override fun visitProperty(property: KtProperty, data: Unit?): EProperty {
        return DeclarationCaster.castProperty(property, castContext)
    }

    override fun visitEnumEntry(enumEntry: KtEnumEntry, data: Unit?): EEnumEntry {
        return DeclarationCaster.castEnumEntry(enumEntry, castContext)
    }

    override fun visitParameter(parameter: KtParameter, data: Unit?): EKtValueParameter {
        return DeclarationCaster.castValueParameter(parameter, castContext)
    }

    override fun visitAnnotatedExpression(expression: KtAnnotatedExpression, data: Unit?): EExpression {
        return castContext.castExpression(expression.baseExpression!!)
    }

    override fun visitParenthesizedExpression(expression: KtParenthesizedExpression, data: Unit?): EExpression {
        return castContext.castExpression(expression.expression!!)
    }

    override fun visitBlockExpression(expression: KtBlockExpression, data: Unit?): EBlockExpression {
        return ExpressionCaster.castBlockExpression(expression, castContext)
    }

    override fun visitPrefixExpression(expression: KtPrefixExpression, data: Unit?): EKtUnaryExpression {
        return ExpressionCaster.castUnaryExpressionPrefix(expression, castContext)
    }

    override fun visitPostfixExpression(expression: KtPostfixExpression, data: Unit?): EKtUnaryExpression {
        return ExpressionCaster.castUnaryExpressionPostfix(expression, castContext)
    }

    override fun visitBinaryExpression(expression: KtBinaryExpression, data: Unit?): EExpression {
        return ExpressionCaster.castBinaryExpressionOrCallExpression(expression, castContext)
    }

    override fun visitSimpleNameExpression(expression: KtSimpleNameExpression, data: Unit?): EReferenceExpression {
        return ExpressionCaster.castReferenceExpression(expression, castContext)
    }

    override fun visitCallExpression(expression: KtCallExpression, data: Unit?): ECallExpression {
        return ExpressionCaster.castCallExpression(expression, castContext)
    }

    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression, data: Unit?): EExpression {
        return ExpressionCaster.castReferenceExpressionOrCallExpression(expression, castContext)
    }

    override fun visitConstantExpression(expression: KtConstantExpression, data: Unit?): EExpression {
        return ExpressionCaster.castConstantExpression(expression, castContext)
    }

    override fun visitThisExpression(expression: KtThisExpression, data: Unit?): EElement {
        return ExpressionCaster.castThisExpression(expression, castContext)
    }

    override fun visitSuperExpression(expression: KtSuperExpression, data: Unit?): EElement {
        return ExpressionCaster.castSuperExpression(expression, castContext)
    }

    override fun visitReturnExpression(expression: KtReturnExpression, data: Unit?): EReturnStatement {
        return ExpressionCaster.castReturnStatement(expression, castContext)
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

    override fun visitArrayAccessExpression(
        expression: KtArrayAccessExpression,
        data: Unit?
    ): EKtArrayAccessExpression {
        return ExpressionCaster.castArrayAccessExpression(expression, castContext)
    }

    override fun visitIsExpression(expression: KtIsExpression, data: Unit?): EElement {
        return ExpressionCaster.castIsExpression(expression, castContext)
    }

    override fun visitBinaryWithTypeRHSExpression(expression: KtBinaryExpressionWithTypeRHS, data: Unit?): EElement {
        return ExpressionCaster.castAsExpression(expression, castContext)
    }

    override fun visitIfExpression(expression: KtIfExpression, data: Unit?): EIfExpression {
        return ExpressionCaster.castIfExpression(expression, castContext)
    }

    override fun visitWhenExpression(expression: KtWhenExpression, data: Unit?): EWhenExpression {
        return WhenExpressionCaster.castWhenExpression(expression, castContext)
    }

    override fun visitWhileExpression(expression: KtWhileExpression, data: Unit?): EWhileStatement {
        return ExpressionCaster.castWhileStatement(expression, castContext)
    }

    override fun visitDoWhileExpression(expression: KtDoWhileExpression, data: Unit?): EWhileStatement {
        return ExpressionCaster.castDoWhileStatement(expression, castContext)
    }

    override fun visitForExpression(expression: KtForExpression, data: Unit?): EKtForStatement? {
        return ExpressionCaster.castForStatement(expression, castContext)
    }
}
