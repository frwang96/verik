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

import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.property.Type
import io.verik.compiler.main.m
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.psi.KtCallExpression

object CallExpressionCaster {

    // TODO cast default arguments
    fun castValueArguments(expression: KtCallExpression, castContext: CastContext): ArrayList<EExpression> {
        val call = castContext.sliceCall[expression.calleeExpression]!!
        val resolvedCall = castContext.sliceResolvedCall[call]!!
        val resolvedValueArguments = resolvedCall.valueArgumentsByIndex!!
        resolvedValueArguments.forEach {
            if (it.arguments.size != 1)
                m.error("Unable to cast resolved value argument", expression)
        }
        val valueArguments = resolvedValueArguments
            .map { it.arguments[0] }
            .map { castContext.casterVisitor.getExpression(it.getArgumentExpression()!!) }
        return ArrayList(valueArguments)
    }

    fun castTypeArguments(expression: KtCallExpression, castContext: CastContext): ArrayList<Type> {
        val call = castContext.sliceCall[expression.calleeExpression]!!
        return if (call.typeArgumentList != null) {
            val typeArguments = call.typeArguments.map {
                castContext.castType(it.typeReference!!)
            }
            ArrayList(typeArguments)
        } else {
            val resolvedCall = castContext.sliceResolvedCall[call]!!
            val typeArgumentMap = resolvedCall.typeArguments
            val descriptor = castContext.sliceReferenceTarget[expression.calleeExpression] as SimpleFunctionDescriptor
            val typeArguments = descriptor.typeParameters.map {
                castContext.castType(typeArgumentMap[it]!!, expression)
            }
            ArrayList(typeArguments)
        }
    }
}
