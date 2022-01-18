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
import org.jetbrains.kotlin.builtins.getReturnTypeFromFunctionType
import org.jetbrains.kotlin.builtins.isFunctionType
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNullableType
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.types.KotlinType

object TypeCaster {

    fun cast(castContext: CastContext, expression: KtExpression): Type {
        val kotlinType = castContext.sliceExpressionTypeInfo[expression]!!.type!!
        return cast(castContext, kotlinType, expression)
    }

    fun cast(castContext: CastContext, type: KotlinType, element: KtElement): Type {
        if (type.isFunctionType) {
            return cast(castContext, type.getReturnTypeFromFunctionType(), element)
        }
        val declarationDescriptor = type.constructor.declarationDescriptor!!
        val declaration = castContext.resolveDeclaration(declarationDescriptor, element)
        val arguments = if (declaration != Core.Kt.C_Enum) {
            type.arguments.map { cast(castContext, it.type, element) }
        } else listOf()
        return Type(declaration, ArrayList(arguments))
    }

    fun cast(castContext: CastContext, typeReference: KtTypeReference): Type {
        val kotlinType: KotlinType = castContext.sliceType[typeReference]
            ?: castContext.sliceAbbreviatedType[typeReference]!!
        if (kotlinType.isFunctionType)
            Messages.UNSUPPORTED_ELEMENT.on(typeReference, "Function type")

        val typeElement = typeReference.typeElement
        val userType = if (typeElement is KtNullableType) {
            typeElement.innerType as KtUserType // ignore nullable type for now
        } else typeElement as KtUserType
        val referenceTarget = castContext.sliceReferenceTarget[userType.referenceExpression!!]!!
        val declaration = castContext.resolveDeclaration(referenceTarget, typeReference)
        val arguments = userType.typeArgumentsAsTypes.map { cast(castContext, it) }
        return Type(declaration, ArrayList(arguments))
    }
}
