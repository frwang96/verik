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

import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.ast.element.common.EValueParameter
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtEnumEntry
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.element.kt.ETypeAlias
import io.verik.compiler.ast.interfaces.cast
import io.verik.compiler.core.common.Core
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtEnumEntry
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtTypeAlias
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.psiUtil.getValueParameters
import org.jetbrains.kotlin.resolve.descriptorUtil.classValueType
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperClassOrAny
import org.jetbrains.kotlin.types.typeUtil.isNullableAny
import org.jetbrains.kotlin.types.typeUtil.representativeUpperBound

object DeclarationCaster {

    fun castKtBasicClass(classOrObject: KtClassOrObject, castContext: CastContext): EKtBasicClass? {
        val descriptor = castContext.sliceClass[classOrObject]!!
        val basicClass = castContext.getDeclaration(descriptor, classOrObject)
            .cast<EKtBasicClass>(classOrObject)
            ?: return null

        val supertype = castContext.castType(descriptor.getSuperClassOrAny().defaultType, classOrObject)
        val typeParameters = classOrObject.typeParameters.mapNotNull {
            castContext.casterVisitor.getElement<ETypeParameter>(it)
        }
        val members = classOrObject.declarations.mapNotNull {
            castContext.casterVisitor.getElement(it)
        }
        val annotations = classOrObject.annotationEntries.mapNotNull {
            AnnotationCaster.castAnnotationEntry(it, castContext)
        }
        val isEnum = classOrObject.hasModifier(KtTokens.ENUM_KEYWORD)
        val valueParameters = classOrObject.getValueParameters().mapNotNull {
            castContext.casterVisitor.getElement<EValueParameter>(it)
        }

        basicClass.supertype = supertype
        typeParameters.forEach { it.parent = basicClass }
        basicClass.typeParameters = ArrayList(typeParameters)
        members.forEach { it.parent = basicClass }
        basicClass.members = ArrayList(members)
        annotations.forEach { it.parent = basicClass }
        basicClass.annotations = annotations
        basicClass.isEnum = isEnum
        valueParameters.forEach { it.parent = basicClass }
        basicClass.valueParameters = ArrayList(valueParameters)
        return basicClass
    }

    fun castKtEnumEntry(enumEntry: KtEnumEntry, castContext: CastContext): EKtEnumEntry? {
        val descriptor = castContext.sliceClass[enumEntry]!!
        val ktEnumEntry = castContext.getDeclaration(descriptor, enumEntry)
            .cast<EKtEnumEntry>(enumEntry)
            ?: return null

        val type = castContext.castType(descriptor.classValueType!!, enumEntry)
        val annotations = enumEntry.annotationEntries.mapNotNull {
            AnnotationCaster.castAnnotationEntry(it, castContext)
        }

        ktEnumEntry.type = type
        annotations.forEach { it.parent = ktEnumEntry }
        ktEnumEntry.annotations = annotations
        return ktEnumEntry
    }

    fun castKtFunction(function: KtNamedFunction, castContext: CastContext): EKtFunction? {
        val descriptor = castContext.sliceFunction[function]!!
        val ktFunction = castContext.getDeclaration(descriptor, function)
            .cast<EKtFunction>(function)
            ?: return null

        val returnType = castContext.castType(descriptor.returnType!!, function)
        val valueParameters = function.valueParameters.mapNotNull {
            castContext.casterVisitor.getElement<EValueParameter>(it)
        }
        val body = function.bodyBlockExpression?.let {
            castContext.casterVisitor.getExpression(it)
        }
        val annotations = function.annotationEntries.mapNotNull {
            AnnotationCaster.castAnnotationEntry(it, castContext)
        }

        ktFunction.returnType = returnType
        valueParameters.forEach { it.parent = ktFunction }
        ktFunction.valueParameters = ArrayList(valueParameters)
        body?.parent = ktFunction
        ktFunction.body = body
        annotations.forEach { it.parent = ktFunction }
        ktFunction.annotations = annotations
        return ktFunction
    }

    fun castKtProperty(property: KtProperty, castContext: CastContext): EKtProperty? {
        val descriptor = castContext.sliceVariable[property]!!
        val ktProperty = castContext.getDeclaration(descriptor, property)
            .cast<EKtProperty>(property)
            ?: return null

        val typeReference = property.typeReference
        val type = if (typeReference != null) castContext.castType(typeReference)
        else castContext.castType(descriptor.type, property)

        val initializer = property.initializer?.let {
            castContext.casterVisitor.getExpression(it)
        }
        val annotations = property.annotationEntries.mapNotNull {
            AnnotationCaster.castAnnotationEntry(it, castContext)
        }

        ktProperty.type = type
        initializer?.parent = ktProperty
        ktProperty.initializer = initializer
        annotations.forEach { it.parent = ktProperty }
        ktProperty.annotations = annotations
        return ktProperty
    }

    fun castTypeAlias(alias: KtTypeAlias, castContext: CastContext): ETypeAlias? {
        val descriptor = castContext.sliceTypeAlias[alias]!!
        val typeAlias = castContext.getDeclaration(descriptor, alias)
            .cast<ETypeAlias>(alias)
            ?: return null

        val type = castContext.castType(alias.getTypeReference()!!)

        typeAlias.type = type
        return typeAlias
    }

    fun castTypeParameter(parameter: KtTypeParameter, castContext: CastContext): ETypeParameter? {
        val descriptor = castContext.sliceTypeParameter[parameter]!!
        val typeParameter = castContext.getDeclaration(descriptor, parameter)
            .cast<ETypeParameter>(parameter)
            ?: return null

        val upperBound = if (descriptor.representativeUpperBound.isNullableAny()) Core.Kt.ANY.toType()
        else castContext.castType(descriptor.representativeUpperBound, parameter)

        typeParameter.upperBound = upperBound
        return typeParameter
    }

    fun castValueParameter(parameter: KtParameter, castContext: CastContext): EValueParameter? {
        val descriptor = castContext.sliceValueParameter[parameter]!!
        val valueParameter = castContext.getDeclaration(descriptor, parameter)
            .cast<EValueParameter>(parameter)
            ?: return null

        val annotations = parameter.annotationEntries.mapNotNull {
            AnnotationCaster.castAnnotationEntry(it, castContext)
        }
        val type = castContext.castType(parameter.typeReference!!)

        annotations.forEach { it.parent = valueParameter }
        valueParameter.annotations = annotations
        valueParameter.type = type
        return valueParameter
    }
}
