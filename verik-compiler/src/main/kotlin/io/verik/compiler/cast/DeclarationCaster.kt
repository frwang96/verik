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

import io.verik.compiler.ast.common.cast
import io.verik.compiler.ast.element.common.EBlockExpression
import io.verik.compiler.ast.element.common.ECallExpression
import io.verik.compiler.ast.element.common.EEnumEntry
import io.verik.compiler.ast.element.common.EProperty
import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.ast.element.kt.EKtClass
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.kt.EKtValueParameter
import io.verik.compiler.ast.element.kt.EPrimaryConstructor
import io.verik.compiler.ast.element.kt.ETypeAlias
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.location
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreConstructorDeclaration
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.kdoc.psi.api.KDoc
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtEnumEntry
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtSuperTypeCallEntry
import org.jetbrains.kotlin.psi.KtTypeAlias
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.resolve.descriptorUtil.classValueType
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperClassOrAny
import org.jetbrains.kotlin.types.typeUtil.isNullableAny
import org.jetbrains.kotlin.types.typeUtil.representativeUpperBound

object DeclarationCaster {

    fun castTypeAlias(alias: KtTypeAlias, castContext: CastContext): ETypeAlias {
        val descriptor = castContext.sliceTypeAlias[alias]!!
        val castedTypeAlias = castContext.resolveDeclaration(descriptor, alias)
            .cast<ETypeAlias>(alias)

        val type = castContext.castType(alias.getTypeReference()!!)
        val typeParameters = alias.typeParameters.mapNotNull {
            castContext.castTypeParameter(it)
        }

        castedTypeAlias.fill(type, typeParameters)
        return castedTypeAlias
    }

    fun castTypeParameter(parameter: KtTypeParameter, castContext: CastContext): ETypeParameter {
        val descriptor = castContext.sliceTypeParameter[parameter]!!
        val castedTypeParameter = castContext.resolveDeclaration(descriptor, parameter)
            .cast<ETypeParameter>(parameter)

        val type = if (descriptor.representativeUpperBound.isNullableAny()) Core.Kt.C_Any.toType()
        else castContext.castType(descriptor.representativeUpperBound, parameter)

        castedTypeParameter.fill(type)
        return castedTypeParameter
    }

    fun castClass(classOrObject: KtClassOrObject, castContext: CastContext): EKtClass {
        val descriptor = castContext.sliceClass[classOrObject]!!
        val castedClass = castContext.resolveDeclaration(descriptor, classOrObject)
            .cast<EKtClass>(classOrObject)

        val type = castContext.castType(descriptor.defaultType, classOrObject)
        val annotationEntries = castAnnotationEntries(classOrObject.annotationEntries, castContext)
        val documentationLines = castDocumentationLines(classOrObject.docComment)
        val superType = castContext.castType(descriptor.getSuperClassOrAny().defaultType, classOrObject)
        val declarations = classOrObject.declarations.mapNotNull {
            castContext.castDeclaration(it)
        }
        val typeParameters = classOrObject.typeParameters.mapNotNull {
            castContext.castTypeParameter(it)
        }
        val isEnum = classOrObject.hasModifier(KtTokens.ENUM_KEYWORD)
        val isAbstract = classOrObject.hasModifier(KtTokens.ABSTRACT_KEYWORD)
        val isObject = classOrObject is KtObjectDeclaration
        val primaryConstructor = when {
            classOrObject.hasExplicitPrimaryConstructor() ->
                castContext.castPrimaryConstructor(classOrObject.primaryConstructor!!)
            classOrObject.hasPrimaryConstructor() && !isObject ->
                castImplicitPrimaryConstructor(classOrObject, castContext)
            else -> null
        }
        val superTypeCallEntry = when (classOrObject.superTypeListEntries.size) {
            0 -> null
            1 -> {
                val superTypeListEntry = classOrObject.superTypeListEntries[0]
                if (superTypeListEntry is KtSuperTypeCallEntry) {
                    castSuperTypeCallExpression(superTypeListEntry, castContext)
                } else {
                    Messages.INTERNAL_ERROR.on(classOrObject, "Super type call entry expected")
                }
            }
            else -> Messages.INTERNAL_ERROR.on(classOrObject, "Multiple inheritance not supported")
        }

        castedClass.fill(
            type = type,
            annotationEntries = annotationEntries,
            documentationLines = documentationLines,
            superType = superType,
            declarations = declarations,
            typeParameters = typeParameters,
            isEnum = isEnum,
            isAbstract = isAbstract,
            isObject = isObject,
            primaryConstructor = primaryConstructor,
            superTypeCallExpression = superTypeCallEntry
        )
        return castedClass
    }

    fun castFunction(function: KtNamedFunction, castContext: CastContext): EKtFunction {
        val descriptor = castContext.sliceFunction[function]!!
        val castedFunction = castContext.resolveDeclaration(descriptor, function)
            .cast<EKtFunction>(function)

        val typeReference = function.typeReference
        val type = if (typeReference != null) {
            castContext.castType(typeReference)
        } else {
            Core.Kt.C_Unit.toType()
        }
        val annotationEntries = castAnnotationEntries(function.annotationEntries, castContext)
        val documentationLines = castDocumentationLines(function.docComment)
        val body = function.bodyBlockExpression?.let {
            castContext.castExpression(it).cast()
        } ?: EBlockExpression.empty(castedFunction.location)
        val valueParameters = function.valueParameters.mapNotNull {
            castContext.castValueParameter(it)
        }
        val typeParameters = function.typeParameters.mapNotNull {
            castContext.castTypeParameter(it)
        }
        val isAbstract = function.hasModifier(KtTokens.ABSTRACT_KEYWORD)
        val isOverride = function.hasModifier(KtTokens.OVERRIDE_KEYWORD)

        castedFunction.fill(
            type = type,
            annotationEntries = annotationEntries,
            documentationLines = documentationLines,
            body = body,
            valueParameters = valueParameters,
            typeParameters = typeParameters,
            isAbstract = isAbstract,
            isOverride = isOverride
        )
        return castedFunction
    }

    fun castPrimaryConstructor(constructor: KtPrimaryConstructor, castContext: CastContext): EPrimaryConstructor {
        val descriptor = castContext.sliceConstructor[constructor]!!
        val castedPrimaryConstructor = castContext.resolveDeclaration(descriptor, constructor)
            .cast<EPrimaryConstructor>(constructor)

        val type = castContext.castType(descriptor.returnType, constructor)
        val valueParameters = constructor.valueParameters.mapNotNull {
            castContext.castValueParameter(it)
        }
        val typeParameters = descriptor.typeParameters.map {
            castContext.resolveDeclaration(it, constructor).cast<ETypeParameter>(constructor)
        }

        castedPrimaryConstructor.fill(type, valueParameters, typeParameters)
        return castedPrimaryConstructor
    }

    private fun castImplicitPrimaryConstructor(
        classOrObject: KtClassOrObject,
        castContext: CastContext
    ): EPrimaryConstructor {
        val descriptor = castContext.sliceClass[classOrObject]!!
        val primaryConstructorDescriptor = descriptor.unsubstitutedPrimaryConstructor!!
        val castedPrimaryConstructor = castContext
            .resolveDeclaration(primaryConstructorDescriptor, classOrObject)
            .cast<EPrimaryConstructor>(classOrObject)

        val type = castContext.castType(primaryConstructorDescriptor.returnType, classOrObject)
        val typeParameters = primaryConstructorDescriptor.typeParameters.map {
            castContext.resolveDeclaration(it, classOrObject).cast<ETypeParameter>(classOrObject)
        }

        castedPrimaryConstructor.fill(type, listOf(), typeParameters)
        return castedPrimaryConstructor
    }

    fun castProperty(property: KtProperty, castContext: CastContext): EProperty {
        val descriptor = castContext.sliceVariable[property]!!
        val castedProperty = castContext.resolveDeclaration(descriptor, property)
            .cast<EProperty>(property)

        val typeReference = property.typeReference
        val type = if (typeReference != null) castContext.castType(typeReference)
        else castContext.castType(descriptor.type, property)

        val annotationEntries = castAnnotationEntries(property.annotationEntries, castContext)
        val documentationLines = castDocumentationLines(property.docComment)
        castedProperty.documentationLines = documentationLines
        val initializer = property.initializer?.let {
            castContext.castExpression(it)
        }
        val isMutable = property.isVar

        castedProperty.fill(
            type,
            annotationEntries,
            documentationLines,
            initializer,
            isMutable
        )
        return castedProperty
    }

    fun castEnumEntry(enumEntry: KtEnumEntry, castContext: CastContext): EEnumEntry {
        val descriptor = castContext.sliceClass[enumEntry]!!
        val castedEnumEntry = castContext.resolveDeclaration(descriptor, enumEntry)
            .cast<EEnumEntry>(enumEntry)

        val type = castContext.castType(descriptor.classValueType!!, enumEntry)
        val annotationEntries = castAnnotationEntries(enumEntry.annotationEntries, castContext)
        val documentationLines = castDocumentationLines(enumEntry.docComment)

        castedEnumEntry.fill(type, annotationEntries, documentationLines)
        return castedEnumEntry
    }

    fun castValueParameter(parameter: KtParameter, castContext: CastContext): EKtValueParameter {
        val propertyDescriptor = castContext.slicePrimaryConstructorParameter[parameter]
        val descriptor = propertyDescriptor ?: castContext.sliceValueParameter[parameter]!!
        val castedValueParameter = castContext.resolveDeclaration(descriptor, parameter)
            .cast<EKtValueParameter>(parameter)

        val typeReference = parameter.typeReference
        val type = if (typeReference != null) {
            castContext.castType(typeReference)
        } else {
            castContext.castType(descriptor.type, parameter)
        }
        val annotationEntries = castAnnotationEntries(parameter.annotationEntries, castContext)
        val isPrimaryConstructorProperty = (propertyDescriptor != null)
        val isMutable = descriptor.isVar

        castedValueParameter.fill(type, annotationEntries, isPrimaryConstructorProperty, isMutable)
        return castedValueParameter
    }

    private fun castAnnotationEntries(
        annotationEntries: List<KtAnnotationEntry>,
        castContext: CastContext
    ): List<AnnotationEntry> {
        return annotationEntries.map {
            AnnotationEntryCaster.castAnnotationEntry(it, castContext)
        }
    }

    private fun castDocumentationLines(docComment: KDoc?): List<String>? {
        if (docComment == null)
            return null
        return docComment.text
            .removePrefix("/**")
            .removeSuffix("*/")
            .trim()
            .lines()
            .map { it.trim().removePrefix("*").removePrefix(" ") }
    }

    private fun castSuperTypeCallExpression(
        superTypeCallEntry: KtSuperTypeCallEntry,
        castContext: CastContext
    ): ECallExpression {
        val location = superTypeCallEntry.location()
        val descriptor = castContext.sliceReferenceTarget[
            superTypeCallEntry.calleeExpression.constructorReferenceExpression!!
        ]!!
        val declaration = castContext.resolveDeclaration(descriptor, superTypeCallEntry)
        val type = if (declaration is CoreConstructorDeclaration) {
            declaration.parent.toType()
        } else {
            (declaration as EPrimaryConstructor).type.copy()
        }
        val valueArguments = CallExpressionCaster.castValueArguments(superTypeCallEntry.calleeExpression, castContext)
        return ECallExpression(
            location,
            type,
            declaration,
            null,
            valueArguments,
            ArrayList()
        )
    }
}
