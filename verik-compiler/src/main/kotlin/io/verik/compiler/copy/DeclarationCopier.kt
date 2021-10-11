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

package io.verik.compiler.copy

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

object DeclarationCopier {

    fun <D : EDeclaration> copyDeclaration(declaration: D, copyContext: CopyContext): D {
        val copiedDeclaration = when (declaration) {
            is EKtBasicClass -> copyKtBasicClass(declaration, copyContext)
            is EKtFunction -> copyKtFunction(declaration, copyContext)
            is EPrimaryConstructor -> copyPrimaryConstructor(declaration, copyContext)
            is EKtProperty -> copyKtProperty(declaration, copyContext)
            is EKtEnumEntry -> copyKtEnumEntry(declaration, copyContext)
            is EKtValueParameter -> copyKtValueParameter(declaration, copyContext)
            else -> {
                Messages.INTERNAL_ERROR.on(declaration, "Unable to copy declaration: $declaration")
                declaration
            }
        }
        @Suppress("UNCHECKED_CAST")
        return copiedDeclaration as D
    }

    private fun copyKtBasicClass(basicClass: EKtBasicClass, copyContext: CopyContext): EKtBasicClass {
        val copiedBasicClass = copyContext.getNotNull(basicClass)
            .cast<EKtBasicClass>(basicClass)
            ?: return basicClass

        val typeParameterString = getTypeParameterString(basicClass, copyContext)
        if (typeParameterString != null)
            copiedBasicClass.name = "${copiedBasicClass.name}_$typeParameterString"

        val superType = copyContext.copyType(basicClass.supertype, basicClass)
        val declarations = basicClass.declarations.map { copyContext.copy(it) }
        val annotations = basicClass.annotations.map { copyContext.copy(it) }
        val primaryConstructor = basicClass.primaryConstructor?.let { copyContext.copy(it) }

        copiedBasicClass.init(
            superType,
            declarations,
            listOf(),
            annotations,
            basicClass.isEnum,
            primaryConstructor
        )
        return copiedBasicClass
    }

    private fun copyKtFunction(function: EKtFunction, copyContext: CopyContext): EKtFunction {
        val copiedFunction = copyContext.getNotNull(function)
            .cast<EKtFunction>(function)
            ?: return function

        val typeParameterString = getTypeParameterString(function, copyContext)
        if (typeParameterString != null)
            copiedFunction.name = "${copiedFunction.name}_$typeParameterString"

        val type = copyContext.copyType(function)
        val body = function.body?.let { copyContext.copy(it) }
        val valueParameters = function.valueParameters.map { copyContext.copy(it) }
        val annotations = function.annotations.map { copyContext.copy(it) }

        copiedFunction.init(type, body, valueParameters, listOf(), annotations)
        return copiedFunction
    }

    private fun copyPrimaryConstructor(
        primaryConstructor: EPrimaryConstructor,
        copyContext: CopyContext
    ): EPrimaryConstructor {
        val copiedPrimaryConstructor = copyContext.getNotNull(primaryConstructor)
            .cast<EPrimaryConstructor>(primaryConstructor)
            ?: return primaryConstructor

        val type = copyContext.copyType(primaryConstructor)
        val valueParameters = primaryConstructor.valueParameters.map { copyContext.copy(it) }

        copiedPrimaryConstructor.init(type, valueParameters, arrayListOf())
        return copiedPrimaryConstructor
    }

    private fun copyKtProperty(property: EKtProperty, copyContext: CopyContext): EKtProperty {
        val copiedProperty = copyContext.getNotNull(property)
            .cast<EKtProperty>(property)
            ?: return property

        val type = copyContext.copyType(property)
        val initializer = property.initializer?.let { copyContext.copy(it) }
        val annotations = property.annotations.map { copyContext.copy(it) }

        copiedProperty.init(type, initializer, annotations)
        return copiedProperty
    }

    private fun copyKtEnumEntry(enumEntry: EKtEnumEntry, copyContext: CopyContext): EKtEnumEntry {
        val copiedEnumEntry = copyContext.getNotNull(enumEntry)
            .cast<EKtEnumEntry>(enumEntry)
            ?: return enumEntry

        val type = copyContext.copyType(enumEntry)
        val annotations = enumEntry.annotations.map { copyContext.copy(it) }

        copiedEnumEntry.init(type, annotations)
        return copiedEnumEntry
    }

    private fun copyKtValueParameter(valueParameter: EKtValueParameter, copyContext: CopyContext): EKtValueParameter {
        val copiedValueParameter = copyContext.getNotNull(valueParameter)
            .cast<EKtValueParameter>(valueParameter)
            ?: return valueParameter

        val type = copyContext.copyType(valueParameter)
        val annotations = valueParameter.annotations.map { copyContext.copy(it) }

        copiedValueParameter.init(type, annotations)
        return copiedValueParameter
    }

    private fun getTypeParameterString(typeParameterized: TypeParameterized, copyContext: CopyContext): String? {
        val typeParameters = typeParameterized.typeParameters.map {
            val typeParameter = it.toType()
            copyContext.bind(typeParameter, it)
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
