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
import io.verik.compiler.ast.property.SuperTypeCallEntry
import io.verik.compiler.core.common.CardinalConstantDeclaration
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
            else -> Messages.INTERNAL_ERROR.on(declaration, "Unable to specialize declaration: $declaration")
        }
        @Suppress("UNCHECKED_CAST")
        return copiedDeclaration as D
    }

    private fun specializeKtBasicClass(
        basicClass: EKtBasicClass,
        specializerContext: SpecializerContext
    ): EKtBasicClass {
        val specializedBasicClass = specializerContext[basicClass, basicClass]
            .cast<EKtBasicClass>()

        val typeParameterString = getTypeParameterString(basicClass, specializerContext)
        if (typeParameterString != null)
            specializedBasicClass.name = "${specializedBasicClass.name}_$typeParameterString"

        val typeParameterContext = specializerContext.typeParameterContext
        val declarations = basicClass.declarations.flatMap { declaration ->
            val typeParameterContexts = specializerContext.matchTypeParameterContexts(declaration, typeParameterContext)
            typeParameterContexts.map {
                specializerContext.typeParameterContext = it
                specializerContext.specialize(declaration)
            }
        }
        specializerContext.typeParameterContext = typeParameterContext

        val type = specializerContext.specializeType(basicClass.type, basicClass)
        val superType = specializerContext.specializeType(basicClass.superType, basicClass)
        val annotations = basicClass.annotations.map { specializerContext.specialize(it) }
        val primaryConstructor = basicClass.primaryConstructor?.let { specializerContext.specialize(it) }
        val superTypeCallEntry = basicClass.superTypeCallEntry?.let { superTypeCallEntry ->
            val reference = superTypeCallEntry.reference
            val forwardedReference = if (reference is EDeclaration) {
                specializerContext[reference, basicClass]
            } else reference
            SuperTypeCallEntry(
                forwardedReference,
                ArrayList(superTypeCallEntry.valueArguments.map { specializerContext.specialize(it) })
            )
        }

        specializedBasicClass.init(
            type,
            superType,
            declarations,
            listOf(),
            annotations,
            basicClass.isEnum,
            basicClass.isAbstract,
            basicClass.isObject,
            primaryConstructor,
            superTypeCallEntry
        )
        return specializedBasicClass
    }

    private fun specializeKtFunction(function: EKtFunction, specializerContext: SpecializerContext): EKtFunction {
        val specializedFunction = specializerContext[function, function]
            .cast<EKtFunction>()

        val typeParameterString = getTypeParameterString(function, specializerContext)
        if (typeParameterString != null)
            specializedFunction.name = "${specializedFunction.name}_$typeParameterString"

        val type = specializerContext.specializeType(function)
        val body = function.body?.let { specializerContext.specialize(it) }
        val valueParameters = function.valueParameters.map { specializerContext.specialize(it) }
        val annotations = function.annotations.map { specializerContext.specialize(it) }

        specializedFunction.init(
            type,
            body,
            valueParameters,
            listOf(),
            annotations,
            function.isAbstract,
            function.isOverridable,
            function.isOverride
        )
        return specializedFunction
    }

    private fun specializePrimaryConstructor(
        primaryConstructor: EPrimaryConstructor,
        specializerContext: SpecializerContext
    ): EPrimaryConstructor {
        val specializedPrimaryConstructor = specializerContext[primaryConstructor, primaryConstructor]
            .cast<EPrimaryConstructor>()

        val type = specializerContext.specializeType(primaryConstructor)
        val valueParameters = primaryConstructor.valueParameters.map { specializerContext.specialize(it) }

        specializedPrimaryConstructor.init(type, valueParameters, arrayListOf())
        return specializedPrimaryConstructor
    }

    private fun specializeKtProperty(property: EKtProperty, specializerContext: SpecializerContext): EKtProperty {
        val specializedProperty = specializerContext[property, property]
            .cast<EKtProperty>()

        val type = specializerContext.specializeType(property)
        val initializer = property.initializer?.let { specializerContext.specialize(it) }
        val annotations = property.annotations.map { specializerContext.specialize(it) }

        specializedProperty.init(type, initializer, annotations, property.isMutable)
        return specializedProperty
    }

    private fun specializeKtEnumEntry(enumEntry: EKtEnumEntry, specializerContext: SpecializerContext): EKtEnumEntry {
        val specializedEnumEntry = specializerContext[enumEntry, enumEntry]
            .cast<EKtEnumEntry>()

        val type = specializerContext.specializeType(enumEntry)
        val annotations = enumEntry.annotations.map { specializerContext.specialize(it) }

        specializedEnumEntry.init(type, annotations)
        return specializedEnumEntry
    }

    private fun specializeKtValueParameter(
        valueParameter: EKtValueParameter,
        specializerContext: SpecializerContext
    ): EKtValueParameter {
        val specializedValueParameter = specializerContext[valueParameter, valueParameter]
            .cast<EKtValueParameter>()

        val type = specializerContext.specializeType(valueParameter)
        val annotations = valueParameter.annotations.map { specializerContext.specialize(it) }

        specializedValueParameter.init(
            type,
            annotations,
            valueParameter.isPrimaryConstructorProperty,
            valueParameter.isMutable
        )
        return specializedValueParameter
    }

    private fun getTypeParameterString(
        typeParameterized: TypeParameterized,
        specializerContext: SpecializerContext
    ): String? {
        val typeParameterTypes = typeParameterized.typeParameters.map {
            specializerContext.typeParameterContext.specialize(it, it)
        }
        return if (typeParameterTypes.isNotEmpty()) {
            typeParameterTypes.joinToString(separator = "_") {
                val reference = it.reference
                if (reference is CardinalConstantDeclaration)
                    reference.value.toString()
                else
                    reference.name
            }
        } else null
    }
}
