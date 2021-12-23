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
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.kt.EKtClass
import io.verik.compiler.ast.element.kt.EKtEnumEntry
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.element.kt.EKtValueParameter
import io.verik.compiler.ast.element.kt.EPrimaryConstructor
import io.verik.compiler.ast.element.kt.ETypeAlias
import io.verik.compiler.ast.interfaces.cast
import io.verik.compiler.ast.property.SuperTypeCallEntry
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.lexer.KtTokens
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
        val castedTypeAlias = castContext.getDeclaration(descriptor, alias)
            .cast<ETypeAlias>(alias)

        val type = castContext.castType(alias.getTypeReference()!!)

        castedTypeAlias.init(type)
        return castedTypeAlias
    }

    fun castTypeParameter(parameter: KtTypeParameter, castContext: CastContext): ETypeParameter {
        val descriptor = castContext.sliceTypeParameter[parameter]!!
        val castedTypeParameter = castContext.getDeclaration(descriptor, parameter)
            .cast<ETypeParameter>(parameter)

        val type = if (descriptor.representativeUpperBound.isNullableAny()) Core.Kt.C_Any.toType()
        else castContext.castType(descriptor.representativeUpperBound, parameter)

        castedTypeParameter.init(type)
        return castedTypeParameter
    }

    fun castKtClass(classOrObject: KtClassOrObject, castContext: CastContext): EKtClass {
        val descriptor = castContext.sliceClass[classOrObject]!!
        val castedClass = castContext.getDeclaration(descriptor, classOrObject)
            .cast<EKtClass>(classOrObject)

        val type = castContext.castType(descriptor.defaultType, classOrObject)
        val superType = castContext.castType(descriptor.getSuperClassOrAny().defaultType, classOrObject)
        val declarations = classOrObject.declarations.mapNotNull {
            castContext.casterVisitor.getDeclaration(it)
        }
        val typeParameters = classOrObject.typeParameters.mapNotNull {
            castContext.casterVisitor.getElement<ETypeParameter>(it)
        }
        val annotationEntries = classOrObject.annotationEntries.mapNotNull {
            AnnotationEntryCaster.castAnnotationEntry(it, castContext)
        }
        val isEnum = classOrObject.hasModifier(KtTokens.ENUM_KEYWORD)
        val isAbstract = classOrObject.hasModifier(KtTokens.ABSTRACT_KEYWORD)
        val isObject = classOrObject is KtObjectDeclaration
        val primaryConstructor = when {
            classOrObject.hasExplicitPrimaryConstructor() ->
                castContext.casterVisitor.getElement(classOrObject.primaryConstructor!!)
            classOrObject.hasPrimaryConstructor() && !isObject ->
                castImplicitPrimaryConstructor(classOrObject, castContext)
            else -> null
        }
        val superTypeCallEntry = when (classOrObject.superTypeListEntries.size) {
            0 -> null
            1 -> {
                val superTypeListEntry = classOrObject.superTypeListEntries[0]
                if (superTypeListEntry is KtSuperTypeCallEntry) {
                    castSuperTypeCallEntry(superTypeListEntry, castContext)
                } else {
                    Messages.INTERNAL_ERROR.on(classOrObject, "Super type call entry expected")
                }
            }
            else -> Messages.INTERNAL_ERROR.on(classOrObject, "Multiple inheritance not supported")
        }

        castedClass.init(
            type,
            superType,
            declarations,
            typeParameters,
            annotationEntries,
            isEnum,
            isAbstract,
            isObject,
            primaryConstructor,
            superTypeCallEntry
        )
        return castedClass
    }

    fun castKtFunction(function: KtNamedFunction, castContext: CastContext): EKtFunction {
        val descriptor = castContext.sliceFunction[function]!!
        val castedFunction = castContext.getDeclaration(descriptor, function)
            .cast<EKtFunction>(function)

        val typeReference = function.typeReference
        val type = if (typeReference != null) {
            castContext.castType(typeReference)
        } else {
            Core.Kt.C_Unit.toType()
        }
        val body = function.bodyBlockExpression?.let {
            castContext.casterVisitor.getExpression(it).cast()
        } ?: EKtBlockExpression.empty(castedFunction.location)
        val valueParameters = function.valueParameters.mapNotNull {
            castContext.casterVisitor.getElement<EKtValueParameter>(it)
        }
        val typeParameters = function.typeParameters.mapNotNull {
            castContext.casterVisitor.getElement<ETypeParameter>(it)
        }
        val annotationEntries = function.annotationEntries.mapNotNull {
            AnnotationEntryCaster.castAnnotationEntry(it, castContext)
        }
        val isAbstract = function.hasModifier(KtTokens.ABSTRACT_KEYWORD)
        val isOverride = function.hasModifier(KtTokens.OVERRIDE_KEYWORD)

        castedFunction.init(
            type,
            body,
            valueParameters,
            typeParameters,
            annotationEntries,
            isAbstract,
            isOverride
        )
        return castedFunction
    }

    fun castPrimaryConstructor(constructor: KtPrimaryConstructor, castContext: CastContext): EPrimaryConstructor {
        val descriptor = castContext.sliceConstructor[constructor]!!
        val castedPrimaryConstructor = castContext.getDeclaration(descriptor, constructor)
            .cast<EPrimaryConstructor>(constructor)

        val type = castContext.castType(descriptor.returnType, constructor)
        val valueParameters = constructor.valueParameters.mapNotNull {
            castContext.casterVisitor.getElement<EKtValueParameter>(it)
        }
        val typeParameters = descriptor.typeParameters.map {
            castContext.getDeclaration(it, constructor).cast<ETypeParameter>(constructor)
        }

        castedPrimaryConstructor.init(type, valueParameters, typeParameters)
        return castedPrimaryConstructor
    }

    private fun castImplicitPrimaryConstructor(
        classOrObject: KtClassOrObject,
        castContext: CastContext
    ): EPrimaryConstructor {
        val descriptor = castContext.sliceClass[classOrObject]!!
        val primaryConstructorDescriptor = descriptor.unsubstitutedPrimaryConstructor!!
        val castedPrimaryConstructor = castContext
            .getDeclaration(primaryConstructorDescriptor, classOrObject)
            .cast<EPrimaryConstructor>(classOrObject)

        val type = castContext.castType(primaryConstructorDescriptor.returnType, classOrObject)
        val typeParameters = primaryConstructorDescriptor.typeParameters.map {
            castContext.getDeclaration(it, classOrObject).cast<ETypeParameter>(classOrObject)
        }

        castedPrimaryConstructor.init(type, listOf(), typeParameters)
        return castedPrimaryConstructor
    }

    fun castKtProperty(property: KtProperty, castContext: CastContext): EKtProperty {
        val descriptor = castContext.sliceVariable[property]!!
        val castedProperty = castContext.getDeclaration(descriptor, property)
            .cast<EKtProperty>(property)

        val typeReference = property.typeReference
        val type = if (typeReference != null) castContext.castType(typeReference)
        else castContext.castType(descriptor.type, property)

        val initializer = property.initializer?.let {
            castContext.casterVisitor.getExpression(it)
        }
        val annotationEntries = property.annotationEntries.mapNotNull {
            AnnotationEntryCaster.castAnnotationEntry(it, castContext)
        }
        val isMutable = property.isVar

        castedProperty.init(type, initializer, annotationEntries, isMutable)
        return castedProperty
    }

    fun castKtEnumEntry(enumEntry: KtEnumEntry, castContext: CastContext): EKtEnumEntry {
        val descriptor = castContext.sliceClass[enumEntry]!!
        val castedEnumEntry = castContext.getDeclaration(descriptor, enumEntry)
            .cast<EKtEnumEntry>(enumEntry)

        val type = castContext.castType(descriptor.classValueType!!, enumEntry)
        val annotationEntries = enumEntry.annotationEntries.mapNotNull {
            AnnotationEntryCaster.castAnnotationEntry(it, castContext)
        }

        castedEnumEntry.init(type, annotationEntries)
        return castedEnumEntry
    }

    fun castValueParameter(parameter: KtParameter, castContext: CastContext): EKtValueParameter {
        val propertyDescriptor = castContext.slicePrimaryConstructorParameter[parameter]
        val descriptor = propertyDescriptor ?: castContext.sliceValueParameter[parameter]!!
        val castedValueParameter = castContext.getDeclaration(descriptor, parameter)
            .cast<EKtValueParameter>(parameter)

        val typeReference = parameter.typeReference
        val type = if (typeReference != null) {
            castContext.castType(typeReference)
        } else {
            castContext.castType(descriptor.type, parameter)
        }
        val annotationEntries = parameter.annotationEntries.mapNotNull {
            AnnotationEntryCaster.castAnnotationEntry(it, castContext)
        }
        val isPrimaryConstructorProperty = (propertyDescriptor != null)
        val isMutable = descriptor.isVar

        castedValueParameter.init(type, annotationEntries, isPrimaryConstructorProperty, isMutable)
        return castedValueParameter
    }

    private fun castSuperTypeCallEntry(
        superTypeCallEntry: KtSuperTypeCallEntry,
        castContext: CastContext
    ): SuperTypeCallEntry {
        val descriptor = castContext.sliceReferenceTarget[
            superTypeCallEntry.calleeExpression.constructorReferenceExpression!!
        ]!!
        val declaration = castContext.getDeclaration(descriptor, superTypeCallEntry)
        val valueArguments = CallExpressionCaster.castValueArguments(superTypeCallEntry.calleeExpression, castContext)
        return SuperTypeCallEntry(declaration, valueArguments)
    }
}
