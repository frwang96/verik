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
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtEnumEntry
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.element.kt.EKtValueParameter
import io.verik.compiler.ast.element.kt.EPrimaryConstructor
import io.verik.compiler.ast.interfaces.TypeParameterized
import io.verik.compiler.ast.interfaces.cast
import io.verik.compiler.core.common.CoreCardinalConstantDeclaration
import io.verik.compiler.message.Messages

object DeclarationSpecializer {

    fun <D : EDeclaration> specializeDeclaration(declaration: D, specializerContext: SpecializerContext): D {
        val copiedDeclaration = when (declaration) {
            is EKtBasicClass -> specializeKtBasicClass(declaration, specializerContext)
            is EKtFunction -> specializeKtFunction(declaration, specializerContext)
            is EPrimaryConstructor -> specializePrimaryConstructor(declaration, specializerContext)
            is EKtProperty -> specializeKtProperty(declaration, specializerContext)
            is EKtEnumEntry -> specializeKtEnumEntry(declaration, specializerContext)
            is EKtValueParameter -> specializeKtValueParameter(declaration, specializerContext)
            else -> {
                Messages.INTERNAL_ERROR.on(declaration, "Unable to specialize declaration: $declaration")
                declaration
            }
        }
        @Suppress("UNCHECKED_CAST")
        return copiedDeclaration as D
    }

    private fun specializeKtBasicClass(
        basicClass: EKtBasicClass,
        specializerContext: SpecializerContext
    ): EKtBasicClass {
        val specializedBasicClass = specializerContext[basicClass]
            .cast<EKtBasicClass>(basicClass)
            ?: return basicClass

        val typeParameterString = getTypeParameterString(basicClass, specializerContext)
        if (typeParameterString != null)
            specializedBasicClass.name = "${specializedBasicClass.name}_$typeParameterString"

        val superType = specializerContext.specializeType(basicClass.supertype, basicClass)
        val declarations = basicClass.declarations.map { specializerContext.specialize(it) }
        val annotations = basicClass.annotations.map { specializerContext.specialize(it) }
        val primaryConstructor = basicClass.primaryConstructor?.let { specializerContext.specialize(it) }

        specializedBasicClass.init(
            superType,
            declarations,
            listOf(),
            annotations,
            basicClass.isEnum,
            primaryConstructor
        )
        return specializedBasicClass
    }

    private fun specializeKtFunction(function: EKtFunction, specializerContext: SpecializerContext): EKtFunction {
        val specializedFunction = specializerContext[function]
            .cast<EKtFunction>(function)
            ?: return function

        val typeParameterString = getTypeParameterString(function, specializerContext)
        if (typeParameterString != null)
            specializedFunction.name = "${specializedFunction.name}_$typeParameterString"

        val type = specializerContext.specializeType(function)
        val body = function.body?.let { specializerContext.specialize(it) }
        val valueParameters = function.valueParameters.map { specializerContext.specialize(it) }
        val annotations = function.annotations.map { specializerContext.specialize(it) }

        specializedFunction.init(type, body, valueParameters, listOf(), annotations)
        return specializedFunction
    }

    private fun specializePrimaryConstructor(
        primaryConstructor: EPrimaryConstructor,
        specializerContext: SpecializerContext
    ): EPrimaryConstructor {
        val specializedPrimaryConstructor = specializerContext[primaryConstructor]
            .cast<EPrimaryConstructor>(primaryConstructor)
            ?: return primaryConstructor

        val type = specializerContext.specializeType(primaryConstructor)
        val valueParameters = primaryConstructor.valueParameters.map { specializerContext.specialize(it) }

        specializedPrimaryConstructor.init(type, valueParameters, arrayListOf())
        return specializedPrimaryConstructor
    }

    private fun specializeKtProperty(property: EKtProperty, specializerContext: SpecializerContext): EKtProperty {
        val specializedProperty = specializerContext[property]
            .cast<EKtProperty>(property)
            ?: return property

        val type = specializerContext.specializeType(property)
        val initializer = property.initializer?.let { specializerContext.specialize(it) }
        val annotations = property.annotations.map { specializerContext.specialize(it) }

        specializedProperty.init(type, initializer, annotations)
        return specializedProperty
    }

    private fun specializeKtEnumEntry(enumEntry: EKtEnumEntry, specializerContext: SpecializerContext): EKtEnumEntry {
        val specializedEnumEntry = specializerContext[enumEntry]
            .cast<EKtEnumEntry>(enumEntry)
            ?: return enumEntry

        val type = specializerContext.specializeType(enumEntry)
        val annotations = enumEntry.annotations.map { specializerContext.specialize(it) }

        specializedEnumEntry.init(type, annotations)
        return specializedEnumEntry
    }

    private fun specializeKtValueParameter(
        valueParameter: EKtValueParameter,
        specializerContext: SpecializerContext
    ): EKtValueParameter {
        val specializedValueParameter = specializerContext[valueParameter]
            .cast<EKtValueParameter>(valueParameter)
            ?: return valueParameter

        val type = specializerContext.specializeType(valueParameter)
        val annotations = valueParameter.annotations.map { specializerContext.specialize(it) }

        specializedValueParameter.init(type, annotations)
        return specializedValueParameter
    }

    private fun getTypeParameterString(
        typeParameterized: TypeParameterized,
        specializerContext: SpecializerContext
    ): String? {
        val typeParameters = typeParameterized.typeParameters.map {
            val typeParameter = it.toType()
            specializerContext.bind(typeParameter, it)
            typeParameter
        }
        return if (typeParameters.isNotEmpty()) {
            typeParameters.joinToString(separator = "_") {
                val reference = it.reference
                if (reference is CoreCardinalConstantDeclaration)
                    reference.value.toString()
                else
                    reference.name
            }
        } else null
    }
}
