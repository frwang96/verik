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

import io.verik.compiler.ast.common.QualifiedName
import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.descriptor.*
import io.verik.compiler.core.CoreClass
import io.verik.compiler.main.messageCollector
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.impl.AbstractTypeAliasDescriptor
import org.jetbrains.kotlin.descriptors.impl.AbstractTypeParameterDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.getImmediateSuperclassNotAny
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter

object TypeCaster {

    fun castType(type: KotlinType, element: PsiElement): Type {
        if (type.isMarkedNullable)
            messageCollector.error("Nullable type not supported: $type", element)
        return if (type.isTypeParameter()) {
            val typeParameterDescriptor = getTypeParameterDescriptor(type)
            Type(typeParameterDescriptor, arrayListOf())
        } else {
            val classDescriptor = getClassDescriptor(type)
            val arguments = type.arguments.map { castType(it.type, element) }
            Type(classDescriptor, ArrayList(arguments))
        }
    }

    fun castType(bindingContext: BindingContext, typeReference: KtTypeReference): Type {
        val type = bindingContext.getSliceContents(BindingContext.TYPE)[typeReference]!!
        if (type.isMarkedNullable)
            messageCollector.error("Nullable type not supported: $type", typeReference)
        if (type.isTypeParameter()) {
            val typeParameterDescriptor = getTypeParameterDescriptor(type)
            return Type(typeParameterDescriptor, arrayListOf())
        }

        val classDescriptor = getClassDescriptor(type)
        return if (classDescriptor == CoreClass.CARDINAL) {
            castCardinalType(bindingContext, typeReference)
        } else {
            val userType = typeReference.typeElement as KtUserType
            val arguments = userType.typeArgumentsAsTypes.map { castType(bindingContext, it) }
            Type(classDescriptor, ArrayList(arguments))
        }
    }

    private fun castCardinalType(bindingContext: BindingContext, typeReference: KtTypeReference): Type {
        val userType = typeReference.typeElement as KtUserType
        val classifierDescriptor = getCardinalClassifierDescriptor(bindingContext, userType)
        val arguments = userType.typeArgumentsAsTypes.map { castCardinalType(bindingContext, it) }
        return Type(classifierDescriptor, ArrayList(arguments))
    }

    private fun getClassDescriptor(type: KotlinType): ClassDescriptor {
        val qualifiedName = QualifiedName(type.getJetTypeFqName(false))
        val name = qualifiedName.toName()
        val superclassDescriptor = type.getImmediateSuperclassNotAny().let {
            if (it != null) getClassDescriptor(it)
            else CoreClass.ANY
        }
        return ClassDescriptor(name, qualifiedName, superclassDescriptor)
    }

    private fun getTypeParameterDescriptor(type: KotlinType): TypeParameterDescriptor {
        val declarationDescriptor = type.constructor.declarationDescriptor!!
        val qualifiedName = QualifiedName(declarationDescriptor.fqNameSafe.toString())
        val name = qualifiedName.toName()
        val classDescriptor = getClassDescriptor(type)
        return TypeParameterDescriptor(name, qualifiedName, classDescriptor)
    }

    private fun getCardinalClassifierDescriptor(
        bindingContext: BindingContext,
        userType: KtUserType
    ): ClassifierDescriptor {
        val referenceExpression = userType.referenceExpression!!
        val referenceTarget = bindingContext
            .getSliceContents(BindingContext.REFERENCE_TARGET)[referenceExpression]!!
        return when (referenceTarget) {
            is AbstractTypeAliasDescriptor -> {
                val simpleType = referenceTarget.defaultType
                val qualifiedName = QualifiedName(simpleType.getJetTypeFqName(false))
                val name = qualifiedName.toName()
                val cardinal = name.name.toIntOrNull()
                if (cardinal != null) {
                    if (cardinal < 1)
                        messageCollector.error("Cardinal must be a positive integer: $cardinal", referenceExpression)
                    CardinalLiteralDescriptor(cardinal)
                } else {
                    CardinalFunctionDescriptor(name, qualifiedName)
                }
            }
            is AbstractTypeParameterDescriptor -> {
                val simpleType = referenceTarget.defaultType
                val typeParameterDescriptor = getTypeParameterDescriptor(simpleType)
                if (typeParameterDescriptor.upperBound != CoreClass.CARDINAL)
                    messageCollector.error("Cardinal type parameter expected", userType)
                typeParameterDescriptor
            }
            else -> {
                messageCollector.error("Cardinal type expected", userType)
                CardinalLiteralDescriptor(1)
            }
        }
    }
}