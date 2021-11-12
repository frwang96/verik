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

import io.verik.compiler.ast.element.common.EReferenceExpression
import org.jetbrains.kotlin.psi.KtExpression

object ReferenceExpressionCaster {

    fun checkSmartCast(
        expression: KtExpression,
        referenceExpression: EReferenceExpression,
        castContext: CastContext
    ) {
        val explicitSmartCasts = castContext.sliceSmartCast[expression]
        if (explicitSmartCasts != null) {
            explicitSmartCasts.type(null)?.let { referenceExpression.type = castContext.castType(it, expression) }
            castContext.smartCastExpressions.add(referenceExpression)
        }
    }
}