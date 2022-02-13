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

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.declaration.common.EAbstractClass
import io.verik.compiler.ast.element.declaration.common.EEnumEntry
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.common.ETypeParameter
import io.verik.compiler.ast.element.declaration.kt.ECompanionObject
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.declaration.kt.EKtFunction
import io.verik.compiler.ast.element.declaration.kt.EKtValueParameter
import io.verik.compiler.ast.element.declaration.kt.EPrimaryConstructor
import io.verik.compiler.ast.element.declaration.kt.ESecondaryConstructor
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EConstantExpression
import io.verik.compiler.ast.element.expression.common.EIfExpression
import io.verik.compiler.ast.element.expression.common.ENothingExpression
import io.verik.compiler.ast.element.expression.common.EPropertyStatement
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.ast.element.expression.common.EReturnStatement
import io.verik.compiler.ast.element.expression.common.ESuperExpression
import io.verik.compiler.ast.element.expression.common.EThisExpression
import io.verik.compiler.ast.element.expression.common.EWhileStatement
import io.verik.compiler.ast.element.expression.kt.EAsExpression
import io.verik.compiler.ast.element.expression.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.expression.kt.EIsExpression
import io.verik.compiler.ast.element.expression.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.expression.kt.EKtUnaryExpression
import io.verik.compiler.ast.element.expression.kt.EStringTemplateExpression
import io.verik.compiler.ast.element.expression.kt.EWhenExpression
import io.verik.compiler.ast.property.ExpressionStringEntry
import io.verik.compiler.ast.property.LiteralStringEntry
import io.verik.compiler.ast.property.WhenEntry
import io.verik.compiler.message.Messages

object SpecializerCopier {

    fun <E : EElement> copy(element: E, typeArguments: List<Type>, specializeContext: SpecializeContext): E {
        val copiedElement = when (element) {
            // Declarations
            is ETypeParameter ->
                copyTypeParameter(element, typeArguments, specializeContext)
            is EKtClass ->
                copyKtClass(element, typeArguments, specializeContext)
            is ECompanionObject ->
                copyCompanionObject(element, typeArguments, specializeContext)
            is EKtFunction ->
                copyKtFunction(element, typeArguments, specializeContext)
            is EPrimaryConstructor ->
                copyPrimaryConstructor(element, typeArguments, specializeContext)
            is ESecondaryConstructor ->
                copySecondaryConstructor(element, typeArguments, specializeContext)
            is EProperty ->
                copyProperty(element, typeArguments, specializeContext)
            is EEnumEntry ->
                copyEnumEntry(element, typeArguments, specializeContext)
            is EKtValueParameter ->
                copyKtValueParameter(element, typeArguments, specializeContext)
            // Expressions
            is ENothingExpression ->
                copyNothingExpression(element)
            is EBlockExpression ->
                copyBlockExpression(element, typeArguments, specializeContext)
            is EPropertyStatement ->
                copyPropertyStatement(element, typeArguments, specializeContext)
            is EKtUnaryExpression ->
                copyKtUnaryExpression(element, typeArguments, specializeContext)
            is EKtBinaryExpression ->
                copyKtBinaryExpression(element, typeArguments, specializeContext)
            is EReferenceExpression ->
                copyReferenceExpression(element, typeArguments, specializeContext)
            is ECallExpression ->
                copyCallExpression(element, typeArguments, specializeContext)
            is EConstantExpression ->
                copyConstantExpression(element)
            is EThisExpression ->
                copyThisExpression(element)
            is ESuperExpression ->
                copySuperExpression(element)
            is EReturnStatement ->
                copyReturnStatement(element, typeArguments, specializeContext)
            is EFunctionLiteralExpression ->
                copyFunctionLiteralExpression(element, typeArguments, specializeContext)
            is EStringTemplateExpression ->
                copyStringTemplateExpression(element, typeArguments, specializeContext)
            is EIsExpression ->
                copyIsExpression(element, typeArguments, specializeContext)
            is EAsExpression ->
                copyAsExpression(element, typeArguments, specializeContext)
            is EIfExpression ->
                copyIfExpression(element, typeArguments, specializeContext)
            is EWhenExpression ->
                copyWhenExpression(element, typeArguments, specializeContext)
            is EWhileStatement ->
                copyWhileStatement(element, typeArguments, specializeContext)
            else ->
                Messages.INTERNAL_ERROR.on(element, "Unable to copy element: $element")
        }
        @Suppress("UNCHECKED_CAST")
        return copiedElement as E
    }

    private fun copyTypeParameter(
        typeParameter: ETypeParameter,
        typeArguments: List<Type>,
        specializeContext: SpecializeContext
    ): ETypeParameter {
        val type = typeParameter.toType()
        val copiedTypeParameter = ETypeParameter(
            typeParameter.location,
            typeParameter.name,
            type
        )
        specializeContext.register(typeParameter, typeArguments, copiedTypeParameter)
        return copiedTypeParameter
    }

    private fun copyKtClass(
        cls: EKtClass,
        typeArguments: List<Type>,
        specializeContext: SpecializeContext
    ): EKtClass {
        val type = cls.type.copy()
        val superType = cls.superType.copy()
        val typeParameters = cls.typeParameters.map { copy(it, typeArguments, specializeContext) }
        val declarations = cls.declarations
            .filter { it !is EAbstractClass }
            .map { copy(it, typeArguments, specializeContext) }
        val primaryConstructor = cls.primaryConstructor?.let { copy(it, typeArguments, specializeContext) }
        val copiedClass = EKtClass(
            location = cls.location,
            bodyStartLocation = cls.bodyStartLocation,
            bodyEndLocation = cls.bodyEndLocation,
            name = cls.name,
            type = type,
            annotationEntries = cls.annotationEntries,
            documentationLines = cls.documentationLines,
            superType = superType,
            typeParameters = ArrayList(typeParameters),
            declarations = ArrayList(declarations),
            primaryConstructor = primaryConstructor,
            isEnum = cls.isEnum,
            isAbstract = cls.isAbstract,
            isObject = cls.isObject
        )
        specializeContext.register(cls, typeArguments, copiedClass)
        return copiedClass
    }

    private fun copyCompanionObject(
        companionObject: ECompanionObject,
        typeArguments: List<Type>,
        specializeContext: SpecializeContext
    ): ECompanionObject {
        val type = companionObject.type.copy()
        val copiedCompanionObject = ECompanionObject(
            companionObject.location,
            type,
            ArrayList()
        )
        specializeContext.register(companionObject, typeArguments, copiedCompanionObject)
        return copiedCompanionObject
    }

    private fun copyKtFunction(
        function: EKtFunction,
        typeArguments: List<Type>,
        specializeContext: SpecializeContext
    ): EKtFunction {
        val type = function.type.copy()
        val body = copy(function.body, typeArguments, specializeContext)
        val valueParameters = function.valueParameters.map { copy(it, typeArguments, specializeContext) }
        val typeParameters = function.typeParameters.map { copy(it, typeArguments, specializeContext) }
        val copiedFunction = EKtFunction(
            location = function.location,
            name = function.name,
            type = type,
            annotationEntries = function.annotationEntries,
            documentationLines = function.documentationLines,
            body = body,
            valueParameters = ArrayList(valueParameters),
            typeParameters = ArrayList(typeParameters),
            isAbstract = function.isAbstract,
            isOverride = function.isOverride
        )
        specializeContext.register(function, typeArguments, copiedFunction)
        return copiedFunction
    }

    private fun copyPrimaryConstructor(
        primaryConstructor: EPrimaryConstructor,
        typeArguments: List<Type>,
        specializeContext: SpecializeContext
    ): EPrimaryConstructor {
        val type = primaryConstructor.type.copy()
        val valueParameters = primaryConstructor.valueParameters.map { copy(it, typeArguments, specializeContext) }
        val superTypeCallExpression = primaryConstructor.superTypeCallExpression
            ?.let { copy(it, typeArguments, specializeContext) }
        val copiedPrimaryConstructor = EPrimaryConstructor(
            primaryConstructor.location,
            primaryConstructor.name,
            type,
            ArrayList(valueParameters),
            superTypeCallExpression
        )
        specializeContext.register(primaryConstructor, typeArguments, copiedPrimaryConstructor)
        return copiedPrimaryConstructor
    }

    private fun copySecondaryConstructor(
        secondaryConstructor: ESecondaryConstructor,
        typeArguments: List<Type>,
        specializeContext: SpecializeContext
    ): ESecondaryConstructor {
        val type = secondaryConstructor.type.copy()
        val body = copy(secondaryConstructor.body, typeArguments, specializeContext)
        val valueParameters = secondaryConstructor.valueParameters.map { copy(it, typeArguments, specializeContext) }
        val superTypeCallExpression = secondaryConstructor.superTypeCallExpression
            ?.let { copy(it, typeArguments, specializeContext) }
        val copiedSecondaryConstructor = ESecondaryConstructor(
            location = secondaryConstructor.location,
            name = secondaryConstructor.name,
            type = type,
            documentationLines = secondaryConstructor.documentationLines,
            body = body,
            valueParameters = ArrayList(valueParameters),
            superTypeCallExpression = superTypeCallExpression
        )
        specializeContext.register(secondaryConstructor, typeArguments, copiedSecondaryConstructor)
        return copiedSecondaryConstructor
    }

    private fun copyProperty(
        property: EProperty,
        typeArguments: List<Type>,
        specializeContext: SpecializeContext
    ): EProperty {
        val type = property.type.copy()
        val initializer = property.initializer?.let { copy(it, typeArguments, specializeContext) }
        val copiedProperty = EProperty(
            location = property.location,
            endLocation = property.endLocation,
            name = property.name,
            type = type,
            annotationEntries = property.annotationEntries,
            documentationLines = property.documentationLines,
            initializer = initializer,
            isMutable = property.isMutable,
            isStatic = property.isStatic
        )
        specializeContext.register(property, typeArguments, copiedProperty)
        return copiedProperty
    }

    private fun copyEnumEntry(
        enumEntry: EEnumEntry,
        typeArguments: List<Type>,
        specializeContext: SpecializeContext
    ): EEnumEntry {
        val type = enumEntry.type.copy()
        val copiedEnumEntry = EEnumEntry(
            location = enumEntry.location,
            name = enumEntry.name,
            type = type,
            annotationEntries = enumEntry.annotationEntries,
            documentationLines = enumEntry.documentationLines,
        )
        specializeContext.register(enumEntry, typeArguments, copiedEnumEntry)
        return copiedEnumEntry
    }

    private fun copyKtValueParameter(
        valueParameter: EKtValueParameter,
        typeArguments: List<Type>,
        specializeContext: SpecializeContext
    ): EKtValueParameter {
        val type = valueParameter.type.copy()
        val expression = valueParameter.expression?.let { copy(it, typeArguments, specializeContext) }
        val copiedValueParameter = EKtValueParameter(
            location = valueParameter.location,
            name = valueParameter.name,
            type = type,
            annotationEntries = valueParameter.annotationEntries,
            expression = expression,
            isPrimaryConstructorProperty = valueParameter.isPrimaryConstructorProperty,
            isMutable = valueParameter.isMutable
        )
        specializeContext.register(valueParameter, typeArguments, copiedValueParameter)
        return copiedValueParameter
    }

    private fun copyNothingExpression(nothingExpression: ENothingExpression): ENothingExpression {
        return ENothingExpression(nothingExpression.location)
    }

    private fun copyBlockExpression(
        blockExpression: EBlockExpression,
        typeArguments: List<Type>,
        specializeContext: SpecializeContext
    ): EBlockExpression {
        val type = blockExpression.type.copy()
        val statements = blockExpression.statements.map { copy(it, typeArguments, specializeContext) }
        return EBlockExpression(
            blockExpression.location,
            blockExpression.endLocation,
            type,
            ArrayList(statements)
        )
    }

    private fun copyPropertyStatement(
        propertyStatement: EPropertyStatement,
        typeArguments: List<Type>,
        specializeContext: SpecializeContext
    ): EPropertyStatement {
        val property = copy(propertyStatement.property, typeArguments, specializeContext)
        return EPropertyStatement(
            propertyStatement.location,
            property
        )
    }

    private fun copyKtUnaryExpression(
        unaryExpression: EKtUnaryExpression,
        typeArguments: List<Type>,
        specializeContext: SpecializeContext
    ): EKtUnaryExpression {
        val type = unaryExpression.type.copy()
        val expression = copy(unaryExpression.expression, typeArguments, specializeContext)
        return EKtUnaryExpression(
            unaryExpression.location,
            type,
            expression,
            unaryExpression.kind
        )
    }

    private fun copyKtBinaryExpression(
        binaryExpression: EKtBinaryExpression,
        typeArguments: List<Type>,
        specializeContext: SpecializeContext
    ): EKtBinaryExpression {
        val type = binaryExpression.type.copy()
        val left = copy(binaryExpression.left, typeArguments, specializeContext)
        val right = copy(binaryExpression.right, typeArguments, specializeContext)
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
        typeArguments: List<Type>,
        specializeContext: SpecializeContext
    ): EReferenceExpression {
        val type = referenceExpression.type.copy()
        val receiver = referenceExpression.receiver?.let { copy(it, typeArguments, specializeContext) }
        return EReferenceExpression(
            referenceExpression.location,
            type,
            referenceExpression.reference,
            receiver
        )
    }

    private fun copyCallExpression(
        callExpression: ECallExpression,
        typeArguments: List<Type>,
        specializeContext: SpecializeContext
    ): ECallExpression {
        val type = callExpression.type.copy()
        val receiver = callExpression.receiver?.let { copy(it, typeArguments, specializeContext) }
        val copiedValueArguments = callExpression.valueArguments.map { copy(it, typeArguments, specializeContext) }
        val copiedTypeArguments = callExpression.typeArguments.map { it.copy() }
        return ECallExpression(
            callExpression.location,
            type,
            callExpression.reference,
            receiver,
            ArrayList(copiedValueArguments),
            ArrayList(copiedTypeArguments)
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
        typeArguments: List<Type>,
        specializeContext: SpecializeContext
    ): EReturnStatement {
        val type = returnStatement.type.copy()
        val expression = returnStatement.expression?.let { copy(it, typeArguments, specializeContext) }
        return EReturnStatement(
            returnStatement.location,
            type,
            expression
        )
    }

    private fun copyFunctionLiteralExpression(
        functionLiteralExpression: EFunctionLiteralExpression,
        typeArguments: List<Type>,
        specializeContext: SpecializeContext
    ): EFunctionLiteralExpression {
        val valueParameters = functionLiteralExpression.valueParameters
            .map { copy(it, typeArguments, specializeContext) }
        val body = copy(functionLiteralExpression.body, typeArguments, specializeContext)
        return EFunctionLiteralExpression(
            functionLiteralExpression.location,
            ArrayList(valueParameters),
            body
        )
    }

    private fun copyStringTemplateExpression(
        stringTemplateExpression: EStringTemplateExpression,
        typeArguments: List<Type>,
        specializeContext: SpecializeContext
    ): EStringTemplateExpression {
        val entries = stringTemplateExpression.entries.map {
            when (it) {
                is LiteralStringEntry ->
                    LiteralStringEntry(it.text)
                is ExpressionStringEntry ->
                    ExpressionStringEntry(copy(it.expression, typeArguments, specializeContext))
            }
        }
        return EStringTemplateExpression(
            stringTemplateExpression.location,
            entries
        )
    }

    private fun copyIsExpression(
        isExpression: EIsExpression,
        typeArguments: List<Type>,
        specializeContext: SpecializeContext
    ): EIsExpression {
        val expression = copy(isExpression.expression, typeArguments, specializeContext)
        val property = copy(isExpression.property, typeArguments, specializeContext)
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
        typeArguments: List<Type>,
        specializeContext: SpecializeContext
    ): EAsExpression {
        val type = asExpression.type.copy()
        val expression = copy(asExpression.expression, typeArguments, specializeContext)
        return EAsExpression(
            asExpression.location,
            type,
            expression
        )
    }

    private fun copyIfExpression(
        ifExpression: EIfExpression,
        typeArguments: List<Type>,
        specializeContext: SpecializeContext
    ): EIfExpression {
        val type = ifExpression.type.copy()
        val condition = copy(ifExpression.condition, typeArguments, specializeContext)
        val thenExpression = ifExpression.thenExpression?.let { copy(it, typeArguments, specializeContext) }
        val elseExpression = ifExpression.elseExpression?.let { copy(it, typeArguments, specializeContext) }
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
        typeArguments: List<Type>,
        specializeContext: SpecializeContext
    ): EWhenExpression {
        val type = whenExpression.type.copy()
        val subject = whenExpression.subject?.let { copy(it, typeArguments, specializeContext) }
        val entries = whenExpression.entries.map { entry ->
            WhenEntry(
                ArrayList(entry.conditions.map { copy(it, typeArguments, specializeContext) }),
                copy(entry.body, typeArguments, specializeContext)
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
        typeArguments: List<Type>,
        specializeContext: SpecializeContext
    ): EWhileStatement {
        val condition = copy(whileStatement.condition, typeArguments, specializeContext)
        val body = copy(whileStatement.body, typeArguments, specializeContext)
        return EWhileStatement(
            whileStatement.location,
            condition,
            body,
            whileStatement.isDoWhile
        )
    }
}
