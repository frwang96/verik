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
import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EEnumEntry
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EIfExpression
import io.verik.compiler.ast.element.common.ENullExpression
import io.verik.compiler.ast.element.common.EPropertyStatement
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.common.EReturnStatement
import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.ast.element.common.EWhileStatement
import io.verik.compiler.ast.element.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.kt.EKtArrayAccessExpression
import io.verik.compiler.ast.element.kt.EKtClass
import io.verik.compiler.ast.element.kt.EKtForStatement
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.element.kt.EKtUnaryExpression
import io.verik.compiler.ast.element.kt.EKtValueParameter
import io.verik.compiler.ast.element.kt.EPrimaryConstructor
import io.verik.compiler.ast.element.kt.EStringTemplateExpression
import io.verik.compiler.ast.element.kt.ETypeAlias
import io.verik.compiler.ast.element.kt.EWhenExpression
import io.verik.compiler.common.location
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.psi.KtAnnotatedExpression
import org.jetbrains.kotlin.psi.KtArrayAccessExpression
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBinaryExpressionWithTypeRHS
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtDoWhileExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtEnumEntry
import org.jetbrains.kotlin.psi.KtExpression
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

    fun getDeclaration(declaration: KtDeclaration): EDeclaration? {
        val location = declaration.location()
        @Suppress("RedundantNullableReturnType")
        val element: EElement? = declaration.accept(this, Unit)
        @Suppress("KotlinConstantConditions")
        return when (element) {
            null -> null
            is EDeclaration -> element
            else -> {
                Messages.INTERNAL_ERROR.on(location, "Declaration expected but got: ${element::class.simpleName}")
            }
        }
    }

    fun getExpression(expression: KtExpression): EExpression {
        val location = expression.location()
        @Suppress("RedundantNullableReturnType")
        val element: EElement? = expression.accept(this, Unit)
        @Suppress("KotlinConstantConditions")
        return when (element) {
            is EKtClass -> {
                Messages.ILLEGAL_LOCAL_DECLARATION.on(element, element.name)
                ENullExpression(location)
            }
            is EKtFunction -> {
                Messages.ILLEGAL_LOCAL_DECLARATION.on(element, element.name)
                ENullExpression(location)
            }
            is EKtProperty -> {
                EPropertyStatement(location, element)
            }
            is EExpression -> element
            null -> ENullExpression(location)
            else -> {
                Messages.INTERNAL_ERROR.on(location, "Expression expected but got: ${element::class.simpleName}")
            }
        }
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
        return DeclarationCaster.castKtClass(classOrObject, castContext)
    }

    override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor, data: Unit?): EPrimaryConstructor {
        return DeclarationCaster.castPrimaryConstructor(constructor, castContext)
    }

    override fun visitNamedFunction(function: KtNamedFunction, data: Unit?): EKtFunction {
        return DeclarationCaster.castKtFunction(function, castContext)
    }

    override fun visitProperty(property: KtProperty, data: Unit?): EKtProperty {
        return DeclarationCaster.castKtProperty(property, castContext)
    }

    override fun visitEnumEntry(enumEntry: KtEnumEntry, data: Unit?): EEnumEntry {
        return DeclarationCaster.castEnumEntry(enumEntry, castContext)
    }

    override fun visitParameter(parameter: KtParameter, data: Unit?): EKtValueParameter {
        return DeclarationCaster.castValueParameter(parameter, castContext)
    }

    override fun visitAnnotatedExpression(expression: KtAnnotatedExpression, data: Unit?): EExpression {
        return getExpression(expression.baseExpression!!)
    }

    override fun visitParenthesizedExpression(expression: KtParenthesizedExpression, data: Unit?): EExpression {
        return getExpression(expression.expression!!)
    }

    override fun visitBlockExpression(expression: KtBlockExpression, data: Unit?): EBlockExpression {
        return ExpressionCaster.castKtBlockExpression(expression, castContext)
    }

    override fun visitPrefixExpression(expression: KtPrefixExpression, data: Unit?): EKtUnaryExpression {
        return ExpressionCaster.castKtUnaryExpressionPrefix(expression, castContext)
    }

    override fun visitPostfixExpression(expression: KtPostfixExpression, data: Unit?): EKtUnaryExpression {
        return ExpressionCaster.castKtUnaryExpressionPostfix(expression, castContext)
    }

    override fun visitBinaryExpression(expression: KtBinaryExpression, data: Unit?): EExpression {
        return ExpressionCaster.castKtBinaryExpressionOrCallExpression(expression, castContext)
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
        return ExpressionCaster.castKtArrayAccessExpression(expression, castContext)
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
        return ExpressionCaster.castKtForStatement(expression, castContext)
    }
}
