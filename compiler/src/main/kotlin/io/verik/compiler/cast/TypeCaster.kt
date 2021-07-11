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

import io.verik.compiler.ast.property.Type
import io.verik.compiler.core.Core
import io.verik.compiler.main.m
import org.jetbrains.kotlin.builtins.isFunctionType
import org.jetbrains.kotlin.descriptors.impl.AbstractTypeParameterDescriptor
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.KotlinType

object TypeCaster {

    fun castFromType(declarationMap: DeclarationMap, type: KotlinType, element: KtElement): Type {
        if (type.isFunctionType)
            return Type(Core.Kt.FUNCTION, ArrayList())
        val declarationDescriptor = type.constructor.declarationDescriptor!!
        val declaration = declarationMap[declarationDescriptor, element]
        val arguments = type.arguments.map { castFromType(declarationMap, it.type, element) }
        return Type(declaration, ArrayList(arguments))
    }

    fun castFromTypeReference(
        bindingContext: BindingContext,
        declarationMap: DeclarationMap,
        typeReference: KtTypeReference
    ): Type {
        val kotlinType = bindingContext.getSliceContents(BindingContext.TYPE)[typeReference]!!
        if (kotlinType.isMarkedNullable)
            m.error("Nullable type not supported: $kotlinType", typeReference)
        if (kotlinType.isFunctionType)
            m.error("Function type not supported: $kotlinType", typeReference)
        val declarationDescriptor = kotlinType.constructor.declarationDescriptor!!
        val declaration = declarationMap[declarationDescriptor, typeReference]
        val userType = typeReference.typeElement as KtUserType
        val arguments = userType.typeArgumentsAsTypes.map { castFromTypeReference(bindingContext, declarationMap, it) }

        val type = Type(declaration, ArrayList(arguments))
        return if (type.isCardinalType()) {
            castCardinalType(bindingContext, declarationMap, typeReference)
        } else type
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
        if (!type.isCardinalType()) {
            if (referenceTarget is AbstractTypeParameterDescriptor)
                m.error("Cardinal type parameter expected", typeReference)
            else
                m.error("Cardinal type expected", typeReference)
        }
        return type
    }
}