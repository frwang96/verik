/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.cast

import io.verik.compiler.ast.common.Type
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.builtins.getReturnTypeFromFunctionType
import org.jetbrains.kotlin.builtins.isFunctionType
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
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
        val declaration = when (
            val declarationDescriptor = castContext.sliceReferenceTarget[userType.referenceExpression!!]!!
        ) {
            is ConstructorDescriptor -> {
                castContext.resolveDeclaration(declarationDescriptor.constructedClass, typeReference)
            }
            else -> {
                castContext.resolveDeclaration(declarationDescriptor, typeReference)
            }
        }
        val arguments = userType.typeArgumentsAsTypes.map { cast(castContext, it) }
        return Type(declaration, ArrayList(arguments))
    }
}
