/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.compiler.specialize

import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EIfExpression
import io.verik.compiler.ast.element.common.EPropertyStatement
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.common.EReturnStatement
import io.verik.compiler.ast.element.common.ESuperExpression
import io.verik.compiler.ast.element.common.EThisExpression
import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.ast.element.common.EWhileStatement
import io.verik.compiler.ast.element.kt.EAsExpression
import io.verik.compiler.ast.element.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.kt.EIsExpression
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtClass
import io.verik.compiler.ast.element.kt.EKtEnumEntry
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.element.kt.EKtUnaryExpression
import io.verik.compiler.ast.element.kt.EKtValueParameter
import io.verik.compiler.ast.element.kt.EPrimaryConstructor
import io.verik.compiler.ast.element.kt.EStringTemplateExpression
import io.verik.compiler.ast.element.kt.EWhenExpression
import io.verik.compiler.ast.property.ExpressionStringEntry
import io.verik.compiler.ast.property.LiteralStringEntry
import io.verik.compiler.ast.property.SuperTypeCallEntry
import io.verik.compiler.ast.property.WhenEntry
import io.verik.compiler.message.Messages

object SpecializerCopier {

    fun <E : EElement> copy(
        element: E,
        typeParameterBindings: List<TypeParameterBinding>,
        specializeContext: SpecializeContext
    ): E {
        val copiedElement = when (element) {
            // Declarations
            is ETypeParameter ->
                copyTypeParameter(element, typeParameterBindings, specializeContext)
            is EKtClass ->
                copyKtClass(element, typeParameterBindings, specializeContext)
            is EKtFunction ->
                copyKtFunction(element, typeParameterBindings, specializeContext)
            is EPrimaryConstructor ->
                copyPrimaryConstructor(element, typeParameterBindings, specializeContext)
            is EKtProperty ->
                copyKtProperty(element, typeParameterBindings, specializeContext)
            is EKtEnumEntry ->
                copyKtEnumEntry(element, typeParameterBindings, specializeContext)
            is EKtValueParameter ->
                copyKtValueParameter(element, typeParameterBindings, specializeContext)
            // Expressions
            is EKtBlockExpression ->
                copyKtBlockExpression(element, typeParameterBindings, specializeContext)
            is EPropertyStatement ->
                copyPropertyStatement(element, typeParameterBindings, specializeContext)
            is EKtUnaryExpression ->
                copyKtUnaryExpression(element, typeParameterBindings, specializeContext)
            is EKtBinaryExpression ->
                copyKtBinaryExpression(element, typeParameterBindings, specializeContext)
            is EReferenceExpression ->
                copyReferenceExpression(element, typeParameterBindings, specializeContext)
            is EKtCallExpression ->
                copyKtCallExpression(element, typeParameterBindings, specializeContext)
            is EConstantExpression ->
                copyConstantExpression(element)
            is EThisExpression ->
                copyThisExpression(element)
            is ESuperExpression ->
                copySuperExpression(element)
            is EReturnStatement ->
                copyReturnStatement(element, typeParameterBindings, specializeContext)
            is EFunctionLiteralExpression ->
                copyFunctionLiteralExpression(element, typeParameterBindings, specializeContext)
            is EStringTemplateExpression ->
                copyStringTemplateExpression(element, typeParameterBindings, specializeContext)
            is EIsExpression ->
                copyIsExpression(element, typeParameterBindings, specializeContext)
            is EAsExpression ->
                copyAsExpression(element, typeParameterBindings, specializeContext)
            is EIfExpression ->
                copyIfExpression(element, typeParameterBindings, specializeContext)
            is EWhenExpression ->
                copyWhenExpression(element, typeParameterBindings, specializeContext)
            is EWhileStatement ->
                copyWhileStatement(element, typeParameterBindings, specializeContext)
            else ->
                Messages.INTERNAL_ERROR.on(element, "Unable to copy element: $element")
        }
        @Suppress("UNCHECKED_CAST")
        return copiedElement as E
    }

    private fun copyTypeParameter(
        typeParameter: ETypeParameter,
        typeParameterBindings: List<TypeParameterBinding>,
        specializeContext: SpecializeContext
    ): ETypeParameter {
        val type = typeParameter.toType()
        val copiedTypeParameter = ETypeParameter(
            typeParameter.location,
            typeParameter.name
        )
        copiedTypeParameter.init(type)
        specializeContext.register(typeParameter, typeParameterBindings, copiedTypeParameter)
        return copiedTypeParameter
    }

    private fun copyKtClass(
        `class`: EKtClass,
        typeParameterBindings: List<TypeParameterBinding>,
        specializeContext: SpecializeContext,
    ): EKtClass {
        val type = `class`.type.copy()
        val superType = `class`.superType.copy()
        val declarations = `class`.declarations.map { copy(it, typeParameterBindings, specializeContext) }
        val typeParameters = `class`.typeParameters.map { copy(it, typeParameterBindings, specializeContext) }
        val primaryConstructor = `class`.primaryConstructor?.let { copy(it, typeParameterBindings, specializeContext) }
        val superTypeCallEntry = `class`.superTypeCallEntry?.let { superTypeCallEntry ->
            SuperTypeCallEntry(
                superTypeCallEntry.reference,
                ArrayList(superTypeCallEntry.valueArguments.map { copy(it, typeParameterBindings, specializeContext) })
            )
        }
        val copiedClass = EKtClass(
            `class`.location,
            `class`.bodyStartLocation,
            `class`.bodyEndLocation,
            `class`.name
        )
        copiedClass.init(
            type,
            superType,
            declarations,
            typeParameters,
            `class`.annotationEntries,
            `class`.isEnum,
            `class`.isAbstract,
            `class`.isObject,
            primaryConstructor,
            superTypeCallEntry
        )
        specializeContext.register(`class`, typeParameterBindings, copiedClass)
        return copiedClass
    }

    private fun copyKtFunction(
        function: EKtFunction,
        typeParameterBindings: List<TypeParameterBinding>,
        specializeContext: SpecializeContext
    ): EKtFunction {
        val type = function.type.copy()
        val body = copy(function.body, typeParameterBindings, specializeContext)
        val valueParameters = function.valueParameters.map { copy(it, typeParameterBindings, specializeContext) }
        val typeParameters = function.typeParameters.map { copy(it, typeParameterBindings, specializeContext) }
        val copiedFunction = EKtFunction(
            function.location,
            function.name
        )
        copiedFunction.init(
            type,
            body,
            valueParameters,
            typeParameters,
            function.annotationEntries,
            function.isOverride,
            function.isOverride
        )
        specializeContext.register(function, typeParameterBindings, copiedFunction)
        return copiedFunction
    }

    private fun copyPrimaryConstructor(
        primaryConstructor: EPrimaryConstructor,
        typeParameterBindings: List<TypeParameterBinding>,
        specializeContext: SpecializeContext
    ): EPrimaryConstructor {
        val type = primaryConstructor.type.copy()
        val valueParameters = primaryConstructor.valueParameters
            .map { copy(it, typeParameterBindings, specializeContext) }
        val typeParameters = primaryConstructor.typeParameters
            .map { copy(it, typeParameterBindings, specializeContext) }
        val copiedPrimaryConstructor = EPrimaryConstructor(primaryConstructor.location)
        copiedPrimaryConstructor.init(type, valueParameters, typeParameters)
        specializeContext.register(primaryConstructor, typeParameterBindings, copiedPrimaryConstructor)
        return copiedPrimaryConstructor
    }

    private fun copyKtProperty(
        property: EKtProperty,
        typeParameterBindings: List<TypeParameterBinding>,
        specializeContext: SpecializeContext
    ): EKtProperty {
        val type = property.type.copy()
        val initializer = property.initializer?.let { copy(it, typeParameterBindings, specializeContext) }
        val copiedProperty = EKtProperty(
            property.location,
            property.endLocation,
            property.name
        )
        copiedProperty.init(
            type,
            initializer,
            property.annotationEntries,
            property.isMutable
        )
        specializeContext.register(property, typeParameterBindings, copiedProperty)
        return copiedProperty
    }

    private fun copyKtEnumEntry(
        enumEntry: EKtEnumEntry,
        typeParameterBindings: List<TypeParameterBinding>,
        specializeContext: SpecializeContext
    ): EKtEnumEntry {
        val type = enumEntry.type.copy()
        val copiedEnumEntry = EKtEnumEntry(enumEntry.location, enumEntry.name)
        copiedEnumEntry.init(type, enumEntry.annotationEntries)
        specializeContext.register(enumEntry, typeParameterBindings, copiedEnumEntry)
        return copiedEnumEntry
    }

    private fun copyKtValueParameter(
        valueParameter: EKtValueParameter,
        typeParameterBindings: List<TypeParameterBinding>,
        specializeContext: SpecializeContext
    ): EKtValueParameter {
        val type = valueParameter.type.copy()
        val copiedValueParameter = EKtValueParameter(valueParameter.location, valueParameter.name)
        copiedValueParameter.init(
            type,
            valueParameter.annotationEntries,
            valueParameter.isPrimaryConstructorProperty,
            valueParameter.isMutable
        )
        specializeContext.register(valueParameter, typeParameterBindings, copiedValueParameter)
        return copiedValueParameter
    }

    private fun copyKtBlockExpression(
        blockExpression: EKtBlockExpression,
        typeParameterBindings: List<TypeParameterBinding>,
        specializeContext: SpecializeContext
    ): EKtBlockExpression {
        val type = blockExpression.type.copy()
        val statements = blockExpression.statements.map { copy(it, typeParameterBindings, specializeContext) }
        return EKtBlockExpression(
            blockExpression.location,
            blockExpression.endLocation,
            type,
            ArrayList(statements)
        )
    }

    private fun copyPropertyStatement(
        propertyStatement: EPropertyStatement,
        typeParameterBindings: List<TypeParameterBinding>,
        specializeContext: SpecializeContext
    ): EPropertyStatement {
        val property = copy(propertyStatement.property, typeParameterBindings, specializeContext)
        return EPropertyStatement(
            propertyStatement.location,
            property
        )
    }

    private fun copyKtUnaryExpression(
        unaryExpression: EKtUnaryExpression,
        typeParameterBindings: List<TypeParameterBinding>,
        specializeContext: SpecializeContext
    ): EKtUnaryExpression {
        val type = unaryExpression.type.copy()
        val expression = copy(unaryExpression.expression, typeParameterBindings, specializeContext)
        return EKtUnaryExpression(
            unaryExpression.location,
            type,
            expression,
            unaryExpression.kind
        )
    }

    private fun copyKtBinaryExpression(
        binaryExpression: EKtBinaryExpression,
        typeParameterBindings: List<TypeParameterBinding>,
        specializeContext: SpecializeContext
    ): EKtBinaryExpression {
        val type = binaryExpression.type.copy()
        val left = copy(binaryExpression.left, typeParameterBindings, specializeContext)
        val right = copy(binaryExpression.right, typeParameterBindings, specializeContext)
        return EKtBinaryExpression(
            binaryExpression.location,
            type,
            left,
            right,
            binaryExpression.kind
        )
    }

    private fun copyReferenceExpression(
        referenceExpression: EReferenceExpression,
        typeParameterBindings: List<TypeParameterBinding>,
        specializeContext: SpecializeContext
    ): EReferenceExpression {
        val type = referenceExpression.type.copy()
        val receiver = referenceExpression.receiver?.let { copy(it, typeParameterBindings, specializeContext) }
        return EReferenceExpression(
            referenceExpression.location,
            type,
            referenceExpression.reference,
            receiver
        )
    }

    private fun copyKtCallExpression(
        callExpression: EKtCallExpression,
        typeParameterBindings: List<TypeParameterBinding>,
        specializeContext: SpecializeContext
    ): EKtCallExpression {
        val type = callExpression.type.copy()
        val receiver = callExpression.receiver?.let { copy(it, typeParameterBindings, specializeContext) }
        val valueArguments = callExpression.valueArguments.map { copy(it, typeParameterBindings, specializeContext) }
        val typeArguments = callExpression.typeArguments.map { it.copy() }
        return EKtCallExpression(
            callExpression.location,
            type,
            callExpression.reference,
            receiver,
            ArrayList(valueArguments),
            ArrayList(typeArguments)
        )
    }

    private fun copyConstantExpression(constantExpression: EConstantExpression): EConstantExpression {
        val type = constantExpression.type.copy()
        return EConstantExpression(
            constantExpression.location,
            type,
            constantExpression.value
        )
    }

    private fun copyThisExpression(thisExpression: EThisExpression): EThisExpression {
        val type = thisExpression.type.copy()
        return EThisExpression(
            thisExpression.location,
            type
        )
    }

    private fun copySuperExpression(superExpression: ESuperExpression): ESuperExpression {
        val type = superExpression.type.copy()
        return ESuperExpression(
            superExpression.location,
            type
        )
    }

    private fun copyReturnStatement(
        returnStatement: EReturnStatement,
        typeParameterBindings: List<TypeParameterBinding>,
        specializeContext: SpecializeContext
    ): EReturnStatement {
        val type = returnStatement.type.copy()
        val expression = returnStatement.expression?.let { copy(it, typeParameterBindings, specializeContext) }
        return EReturnStatement(
            returnStatement.location,
            type,
            expression
        )
    }

    private fun copyFunctionLiteralExpression(
        functionLiteralExpression: EFunctionLiteralExpression,
        typeParameterBindings: List<TypeParameterBinding>,
        specializeContext: SpecializeContext
    ): EFunctionLiteralExpression {
        val valueParameters = functionLiteralExpression.valueParameters
            .map { copy(it, typeParameterBindings, specializeContext) }
        val body = copy(functionLiteralExpression.body, typeParameterBindings, specializeContext)
        return EFunctionLiteralExpression(
            functionLiteralExpression.location,
            ArrayList(valueParameters),
            body
        )
    }

    private fun copyStringTemplateExpression(
        stringTemplateExpression: EStringTemplateExpression,
        typeParameterBindings: List<TypeParameterBinding>,
        specializeContext: SpecializeContext
    ): EStringTemplateExpression {
        val entries = stringTemplateExpression.entries.map {
            when (it) {
                is LiteralStringEntry ->
                    LiteralStringEntry(it.text)
                is ExpressionStringEntry ->
                    ExpressionStringEntry(copy(it.expression, typeParameterBindings, specializeContext))
            }
        }
        return EStringTemplateExpression(
            stringTemplateExpression.location,
            entries
        )
    }

    private fun copyIsExpression(
        isExpression: EIsExpression,
        typeParameterBindings: List<TypeParameterBinding>,
        specializeContext: SpecializeContext
    ): EIsExpression {
        val expression = copy(isExpression.expression, typeParameterBindings, specializeContext)
        val property = copy(isExpression.property, typeParameterBindings, specializeContext)
        val castType = isExpression.castType.copy()
        return EIsExpression(
            isExpression.location,
            expression,
            property,
            isExpression.isNegated,
            castType
        )
    }

    private fun copyAsExpression(
        asExpression: EAsExpression,
        typeParameterBindings: List<TypeParameterBinding>,
        specializeContext: SpecializeContext
    ): EAsExpression {
        val type = asExpression.type.copy()
        val expression = copy(asExpression.expression, typeParameterBindings, specializeContext)
        return EAsExpression(
            asExpression.location,
            type,
            expression
        )
    }

    private fun copyIfExpression(
        ifExpression: EIfExpression,
        typeParameterBindings: List<TypeParameterBinding>,
        specializeContext: SpecializeContext
    ): EIfExpression {
        val type = ifExpression.type.copy()
        val condition = copy(ifExpression.condition, typeParameterBindings, specializeContext)
        val thenExpression = ifExpression.thenExpression?.let { copy(it, typeParameterBindings, specializeContext) }
        val elseExpression = ifExpression.elseExpression?.let { copy(it, typeParameterBindings, specializeContext) }
        return EIfExpression(
            ifExpression.location,
            type,
            condition,
            thenExpression,
            elseExpression
        )
    }

    private fun copyWhenExpression(
        whenExpression: EWhenExpression,
        typeParameterBindings: List<TypeParameterBinding>,
        specializeContext: SpecializeContext
    ): EWhenExpression {
        val type = whenExpression.type.copy()
        val subject = whenExpression.subject?.let { copy(it, typeParameterBindings, specializeContext) }
        val entries = whenExpression.entries.map { entry ->
            WhenEntry(
                ArrayList(entry.conditions.map { copy(it, typeParameterBindings, specializeContext) }),
                copy(entry.body, typeParameterBindings, specializeContext)
            )
        }
        return EWhenExpression(
            whenExpression.location,
            whenExpression.endLocation,
            type,
            subject,
            entries
        )
    }

    private fun copyWhileStatement(
        whileStatement: EWhileStatement,
        typeParameterBindings: List<TypeParameterBinding>,
        specializeContext: SpecializeContext
    ): EWhileStatement {
        val condition = copy(whileStatement.condition, typeParameterBindings, specializeContext)
        val body = copy(whileStatement.body, typeParameterBindings, specializeContext)
        return EWhileStatement(
            whileStatement.location,
            condition,
            body,
            whileStatement.isDoWhile
        )
    }
}
