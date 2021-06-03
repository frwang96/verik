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
import io.verik.compiler.core.CoreClass
import io.verik.compiler.main.m
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.KotlinType

object TypeCaster {

    fun castType(declarationMap: DeclarationMap, type: KotlinType, element: PsiElement): Type {
        if (type.isMarkedNullable)
            m.error("Nullable type not supported: $type", element)
        val declarationDescriptor = type.constructor.declarationDescriptor!!
        val declaration = declarationMap[declarationDescriptor, element]
        val arguments = type.arguments.map { castType(declarationMap, it.type, element) }
        return Type(declaration, ArrayList(arguments))
    }

    fun castType(bindingContext: BindingContext, declarationMap: DeclarationMap, typeReference: KtTypeReference): Type {
        val type = bindingContext.getSliceContents(BindingContext.TYPE)[typeReference]!!
        if (type.isMarkedNullable)
            m.error("Nullable type not supported: $type", typeReference)
        val declarationDescriptor = type.constructor.declarationDescriptor!!
        val declaration = declarationMap[declarationDescriptor, typeReference]
        return if (declaration == CoreClass.CARDINAL) {
            castCardinalType(bindingContext, declarationMap, typeReference)
        } else {
            val userType = typeReference.typeElement as KtUserType
            val arguments = userType.typeArgumentsAsTypes.map { castType(bindingContext, declarationMap, it) }
            Type(declaration, ArrayList(arguments))
        }
    }

    private fun castCardinalType(
        bindingContext: BindingContext,
        declarationMap: DeclarationMap,
        typeReference: KtTypeReference
    ): Type {
        val userType = typeReference.typeElement as KtUserType
        val referenceExpression = userType.referenceExpression!!
        val referenceTarget = bindingContext.getSliceContents(BindingContext.REFERENCE_TARGET)[referenceExpression]!!
        val declaration = declarationMap[referenceTarget, typeReference]
        val arguments = userType.typeArgumentsAsTypes.map { castCardinalType(bindingContext, declarationMap, it) }
        val type = Type(declaration, ArrayList(arguments))
        if (type.toClassType() != CoreClass.CARDINAL.toNoArgumentsType())
            m.error("Cardinal type expected", typeReference)
        return type
    }
}