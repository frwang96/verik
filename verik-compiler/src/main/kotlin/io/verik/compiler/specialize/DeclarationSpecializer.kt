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

package io.verik.compiler.specialize

import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.kt.EKtClass
import io.verik.compiler.ast.element.kt.EKtEnumEntry
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.element.kt.EKtValueParameter
import io.verik.compiler.ast.element.kt.EPrimaryConstructor
import io.verik.compiler.ast.interfaces.TypeParameterized
import io.verik.compiler.ast.property.SuperTypeCallEntry
import io.verik.compiler.core.common.CardinalConstantDeclaration
import io.verik.compiler.message.Messages

object DeclarationSpecializer {

    fun <D : EDeclaration> specializeDeclaration(declaration: D, specializeContext: SpecializeContext): D {
        val copiedDeclaration = when (declaration) {
            is EKtClass -> specializeKtClass(declaration, specializeContext)
            is EKtFunction -> specializeKtFunction(declaration, specializeContext)
            is EPrimaryConstructor -> specializePrimaryConstructor(declaration, specializeContext)
            is EKtProperty -> specializeKtProperty(declaration, specializeContext)
            is EKtEnumEntry -> specializeKtEnumEntry(declaration, specializeContext)
            is EKtValueParameter -> specializeKtValueParameter(declaration, specializeContext)
            else -> Messages.INTERNAL_ERROR.on(declaration, "Unable to specialize declaration: $declaration")
        }
        @Suppress("UNCHECKED_CAST")
        return copiedDeclaration as D
    }

    private fun specializeKtClass(`class`: EKtClass, specializeContext: SpecializeContext): EKtClass {
        val specializedClass = specializeContext[`class`, `class`]
            .cast<EKtClass>()

        val typeParameterString = getTypeParameterString(`class`, specializeContext)
        if (typeParameterString != null)
            specializedClass.name = "${specializedClass.name}_$typeParameterString"

        val typeParameterContext = specializeContext.typeParameterContext
        val declarations = `class`.declarations.flatMap { declaration ->
            val typeParameterContexts = specializeContext.matchTypeParameterContexts(declaration, typeParameterContext)
            typeParameterContexts.map {
                specializeContext.typeParameterContext = it
                specializeContext.specialize(declaration)
            }
        }
        specializeContext.typeParameterContext = typeParameterContext

        val type = specializeContext.specializeType(`class`.type, `class`)
        val superType = specializeContext.specializeType(`class`.superType, `class`)
        val primaryConstructor = `class`.primaryConstructor?.let { specializeContext.specialize(it) }
        val superTypeCallEntry = `class`.superTypeCallEntry?.let { superTypeCallEntry ->
            val reference = superTypeCallEntry.reference
            val forwardedReference = if (reference is EDeclaration) {
                specializeContext[reference, `class`]
            } else reference
            SuperTypeCallEntry(
                forwardedReference,
                ArrayList(superTypeCallEntry.valueArguments.map { specializeContext.specialize(it) })
            )
        }

        specializedClass.init(
            type,
            superType,
            declarations,
            listOf(),
            `class`.annotationEntries,
            `class`.isEnum,
            `class`.isAbstract,
            `class`.isObject,
            primaryConstructor,
            superTypeCallEntry
        )
        return specializedClass
    }

    private fun specializeKtFunction(function: EKtFunction, specializeContext: SpecializeContext): EKtFunction {
        val specializedFunction = specializeContext[function, function]
            .cast<EKtFunction>()

        val typeParameterString = getTypeParameterString(function, specializeContext)
        if (typeParameterString != null)
            specializedFunction.name = "${specializedFunction.name}_$typeParameterString"

        val type = specializeContext.specializeType(function)
        val body = specializeContext.specialize(function.body)
        val valueParameters = function.valueParameters.map { specializeContext.specialize(it) }

        specializedFunction.init(
            type,
            body,
            valueParameters,
            listOf(),
            function.annotationEntries,
            function.isAbstract,
            function.isOverride
        )
        return specializedFunction
    }

    private fun specializePrimaryConstructor(
        primaryConstructor: EPrimaryConstructor,
        specializeContext: SpecializeContext
    ): EPrimaryConstructor {
        val specializedPrimaryConstructor = specializeContext[primaryConstructor, primaryConstructor]
            .cast<EPrimaryConstructor>()
        val type = specializeContext.specializeType(primaryConstructor)
        val valueParameters = primaryConstructor.valueParameters.map { specializeContext.specialize(it) }

        specializedPrimaryConstructor.init(type, valueParameters, arrayListOf())
        return specializedPrimaryConstructor
    }

    private fun specializeKtProperty(property: EKtProperty, specializeContext: SpecializeContext): EKtProperty {
        val specializedProperty = specializeContext[property, property]
            .cast<EKtProperty>()
        val type = specializeContext.specializeType(property)
        val initializer = property.initializer?.let { specializeContext.specialize(it) }

        specializedProperty.init(type, initializer, property.annotationEntries, property.isMutable)
        return specializedProperty
    }

    private fun specializeKtEnumEntry(enumEntry: EKtEnumEntry, specializeContext: SpecializeContext): EKtEnumEntry {
        val specializedEnumEntry = specializeContext[enumEntry, enumEntry]
            .cast<EKtEnumEntry>()
        val type = specializeContext.specializeType(enumEntry)

        specializedEnumEntry.init(type, enumEntry.annotationEntries)
        return specializedEnumEntry
    }

    private fun specializeKtValueParameter(
        valueParameter: EKtValueParameter,
        specializeContext: SpecializeContext
    ): EKtValueParameter {
        val specializedValueParameter = specializeContext[valueParameter, valueParameter]
            .cast<EKtValueParameter>()
        val type = specializeContext.specializeType(valueParameter)

        specializedValueParameter.init(
            type,
            valueParameter.annotationEntries,
            valueParameter.isPrimaryConstructorProperty,
            valueParameter.isMutable
        )
        return specializedValueParameter
    }

    private fun getTypeParameterString(
        typeParameterized: TypeParameterized,
        specializeContext: SpecializeContext
    ): String? {
        val typeParameterTypeStrings = typeParameterized.typeParameters.map {
            val typeParameterType = specializeContext.typeParameterContext.specialize(it, it)
            val reference = typeParameterType.reference
            if (reference is CardinalConstantDeclaration) {
                "${it.name}_${reference.value}"
            } else {
                "${it.name}_${reference.name}"
            }
        }
        return if (typeParameterTypeStrings.isNotEmpty()) {
            typeParameterTypeStrings.joinToString(separator = "_")
        } else null
    }
}
