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
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.builtins.isFunctionType
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.types.KotlinType

object TypeCaster {

    fun cast(castContext: CastContext, expression: KtExpression): Type {
        val kotlinType = castContext.sliceExpressionTypeInfo[expression]!!.type!!
        return cast(castContext, kotlinType, expression)
    }

    fun cast(castContext: CastContext, type: KotlinType, element: KtElement): Type {
        if (type.isFunctionType)
            return Type(Core.Kt.C_Function, ArrayList())
        val declarationDescriptor = type.constructor.declarationDescriptor!!
        val declaration = castContext.getDeclaration(declarationDescriptor, element)
        val arguments = if (declaration != Core.Kt.C_Enum) {
            type.arguments.map { cast(castContext, it.type, element) }
        } else listOf()
        return Type(declaration, ArrayList(arguments))
    }

    fun cast(castContext: CastContext, typeReference: KtTypeReference): Type {
        val kotlinType: KotlinType = castContext.sliceType[typeReference]
            ?: castContext.sliceAbbreviatedType[typeReference]!!
        if (kotlinType.isMarkedNullable)
            Messages.ELEMENT_NOT_SUPPORTED.on(typeReference, "Nullable type")
        if (kotlinType.isFunctionType)
            Messages.ELEMENT_NOT_SUPPORTED.on(typeReference, "Function type")
        val declarationDescriptor = kotlinType.constructor.declarationDescriptor!!
        val declaration = castContext.getDeclaration(declarationDescriptor, typeReference)
        val userType = typeReference.typeElement as KtUserType
        val arguments = userType.typeArgumentsAsTypes.map { cast(castContext, it) }

        val type = Type(declaration, ArrayList(arguments))
        return if (type.isCardinalType()) {
            castCardinalType(castContext, typeReference)
        } else type
    }

    private fun castCardinalType(castContext: CastContext, typeReference: KtTypeReference): Type {
        val userType = typeReference.typeElement as KtUserType
        val referenceExpression = userType.referenceExpression!!
        val referenceTarget = castContext.sliceReferenceTarget[referenceExpression]!!
        val declaration = castContext.getDeclaration(referenceTarget, typeReference)
        val arguments = userType.typeArgumentsAsTypes.map { castCardinalType(castContext, it) }
        return Type(declaration, ArrayList(arguments))
    }
}
