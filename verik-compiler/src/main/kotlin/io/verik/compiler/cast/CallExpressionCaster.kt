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
import org.jetbrains.kotlin.psi.KtCallExpression

object CallExpressionCaster {

    fun castValueArguments(expression: KtCallExpression, castContext: CastContext): ArrayList<EExpression> {
        val valueArguments = expression.valueArguments.map {
            castContext.casterVisitor.getExpression(it.getArgumentExpression()!!)
        }
        return ArrayList(valueArguments)
    }

    fun castTypeArguments(expression: KtCallExpression, castContext: CastContext): ArrayList<Type>? {
        val typeArgumentList = expression.typeArgumentList
        return if (typeArgumentList != null) {
            val typeArguments = typeArgumentList.arguments.map {
                castContext.castType(it.typeReference!!)
            }
            ArrayList(typeArguments)
        } else null
    }
}