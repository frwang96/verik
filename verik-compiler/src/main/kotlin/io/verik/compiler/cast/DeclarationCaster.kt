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

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.common.cast
import io.verik.compiler.ast.element.declaration.common.EEnumEntry
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.common.ETypeParameter
import io.verik.compiler.ast.element.declaration.kt.ECompanionObject
import io.verik.compiler.ast.element.declaration.kt.EInitializerBlock
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.declaration.kt.EKtFunction
import io.verik.compiler.ast.element.declaration.kt.EKtValueParameter
import io.verik.compiler.ast.element.declaration.kt.EPrimaryConstructor
import io.verik.compiler.ast.element.declaration.kt.ESecondaryConstructor
import io.verik.compiler.ast.element.declaration.kt.ETypeAlias
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.location
import io.verik.compiler.core.common.Core
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.kdoc.psi.api.KDoc
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtClassInitializer
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtConstructorDelegationCall
import org.jetbrains.kotlin.psi.KtEnumEntry
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.KtSuperTypeCallEntry
import org.jetbrains.kotlin.psi.KtSuperTypeListEntry
import org.jetbrains.kotlin.psi.KtTypeAlias
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.resolve.descriptorUtil.classValueType
import org.jetbrains.kotlin.resolve.descriptorUtil.overriddenTreeUniqueAsSequence
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
        val superType = castSuperType(classOrObject, castContext)
        val typeParameters = classOrObject.typeParameters.mapNotNull {
            castContext.castTypeParameter(it)
        }
        val declarations = classOrObject.declarations.mapNotNull {
            castContext.castDeclaration(it)
        }
        val primaryConstructor = when {
            classOrObject.hasExplicitPrimaryConstructor() ->
                castContext.castPrimaryConstructor(classOrObject.primaryConstructor!!)
            classOrObject.hasPrimaryConstructor() && classOrObject !is KtObjectDeclaration ->
                castImplicitPrimaryConstructor(classOrObject, castContext)
            else -> null
        }
        val isEnum = classOrObject.hasModifier(KtTokens.ENUM_KEYWORD)
        val isObject = classOrObject is KtObjectDeclaration

        castedClass.fill(
            type = type,
            annotationEntries = annotationEntries,
            documentationLines = documentationLines,
            superType = superType,
            typeParameters = typeParameters,
            declarations = declarations,
            primaryConstructor = primaryConstructor,
            isEnum = isEnum,
            isObject = isObject,
        )
        return castedClass
    }

    fun castCompanionObject(objectDeclaration: KtObjectDeclaration, castContext: CastContext): ECompanionObject {
        val descriptor = castContext.sliceClass[objectDeclaration]!!
        val castedCompanionObject = castContext.resolveDeclaration(descriptor, objectDeclaration)
            .cast<ECompanionObject>(objectDeclaration)

        val type = castContext.castType(descriptor.defaultType, objectDeclaration)
        val declarations = objectDeclaration.declarations.mapNotNull {
            castContext.castDeclaration(it)
        }

        castedCompanionObject.fill(type, declarations)
        return castedCompanionObject
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
        val overriddenTree = descriptor.overriddenTreeUniqueAsSequence(true).toList()
        val overriddenFunction = if (overriddenTree.size > 1) {
            castContext.resolveDeclaration(overriddenTree[1], function)
        } else null

        castedFunction.fill(
            type = type,
            annotationEntries = annotationEntries,
            documentationLines = documentationLines,
            body = body,
            valueParameters = valueParameters,
            typeParameters = typeParameters,
            overriddenFunction = overriddenFunction
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

        val classOrObject = constructor.parent
        val superTypeCallExpression = if (classOrObject is KtClassOrObject) {
            castSuperTypeCallExpression(classOrObject.superTypeListEntries, castContext)
        } else null

        castedPrimaryConstructor.fill(type, valueParameters, superTypeCallExpression)
        return castedPrimaryConstructor
    }

    fun castSecondaryConstructor(constructor: KtSecondaryConstructor, castContext: CastContext): ESecondaryConstructor {
        val descriptor = castContext.sliceConstructor[constructor]!!
        val castedSecondaryConstructor = castContext.resolveDeclaration(descriptor, constructor)
            .cast<ESecondaryConstructor>(constructor)

        val type = castContext.castType(descriptor.returnType, constructor)
        val documentationLines = castDocumentationLines(constructor.docComment)
        val body = constructor.bodyBlockExpression?.let {
            castContext.castExpression(it).cast()
        } ?: EBlockExpression.empty(castedSecondaryConstructor.location)
        val valueParameters = constructor.valueParameters.mapNotNull {
            castContext.castValueParameter(it)
        }
        val superTypeCallExpression = if (!constructor.hasImplicitDelegationCall()) {
            castSuperTypeCallExpression(constructor.getDelegationCall(), castContext)
        } else null

        castedSecondaryConstructor.fill(
            type,
            documentationLines,
            body,
            valueParameters,
            superTypeCallExpression
        )
        return castedSecondaryConstructor
    }

    fun castInitializerBlock(initializer: KtClassInitializer, castContext: CastContext): EInitializerBlock {
        val location = initializer.location()
        val body = castContext.castExpression(initializer.body!!).cast<EBlockExpression>()
        return EInitializerBlock(location, body)
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
        val expression = parameter.defaultValue?.let { castContext.castExpression(it) }
        val isPrimaryConstructorProperty = (propertyDescriptor != null)
        val isMutable = descriptor.isVar

        castedValueParameter.fill(
            type = type,
            annotationEntries = annotationEntries,
            expression = expression,
            isPrimaryConstructorProperty = isPrimaryConstructorProperty,
            isMutable = isMutable
        )
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
        val superTypeListEntry = castSuperTypeCallExpression(classOrObject.superTypeListEntries, castContext)

        castedPrimaryConstructor.fill(type, listOf(), superTypeListEntry)
        return castedPrimaryConstructor
    }

    private fun castSuperType(
        classOrObject: KtClassOrObject,
        castContext: CastContext
    ): Type {
        if (classOrObject.hasModifier(KtTokens.ENUM_KEYWORD)) return Core.Kt.C_Enum.toType()
        val superTypeListEntry = classOrObject.superTypeListEntries.firstOrNull()
        return if (superTypeListEntry != null) {
            castContext.castType(superTypeListEntry.typeReference!!)
        } else Core.Kt.C_Any.toType()
    }

    private fun castSuperTypeCallExpression(
        superTypeListEntries: List<KtSuperTypeListEntry>,
        castContext: CastContext
    ): ECallExpression? {
        superTypeListEntries.forEach {
            if (it is KtSuperTypeCallEntry) {
                return castSuperTypeCallExpression(it, castContext)
            }
        }
        return null
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
        val type = castContext.castType(superTypeCallEntry.typeReference!!)
        val typeArguments = type.arguments.map { it.copy() }
        val valueArguments = CallExpressionCaster.castValueArguments(superTypeCallEntry.calleeExpression, castContext)
        return ECallExpression(
            location,
            type,
            declaration,
            null,
            valueArguments,
            ArrayList(typeArguments)
        )
    }

    private fun castSuperTypeCallExpression(
        constructorDelegationCall: KtConstructorDelegationCall,
        castContext: CastContext
    ): ECallExpression {
        val location = constructorDelegationCall.location()
        val descriptor = castContext.sliceReferenceTarget[
            constructorDelegationCall.calleeExpression!!
        ]!! as ConstructorDescriptor
        val declaration = castContext.resolveDeclaration(descriptor, constructorDelegationCall)
        val typeArguments = CallExpressionCaster.castTypeArguments(constructorDelegationCall, castContext)
        val typeReference = castContext
            .resolveDeclaration(descriptor.constructedClass, constructorDelegationCall)
        val type = typeReference.toType(typeArguments.map { it.copy() })
        val valueArguments = CallExpressionCaster.castValueArguments(
            constructorDelegationCall.calleeExpression!!,
            castContext
        )
        return ECallExpression(
            location,
            type,
            declaration,
            null,
            valueArguments,
            typeArguments
        )
    }
}
