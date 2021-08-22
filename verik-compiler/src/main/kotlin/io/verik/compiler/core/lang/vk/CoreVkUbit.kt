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

package io.verik.compiler.core.lang.vk

import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreCardinalConstantDeclaration
import io.verik.compiler.core.common.CoreKtFunctionDeclaration
import io.verik.compiler.core.common.CoreScope
import java.lang.Integer.max

object CoreVkUbit : CoreScope(Core.Vk.UBIT) {

    val INV = CoreKtFunctionDeclaration(parent, "inv")

    val PLUS_UBIT = object : CoreKtFunctionDeclaration(parent, "plus", Core.Vk.UBIT) {

        // TODO generalize resolution
        override fun resolve(callExpression: EKtCallExpression) {
            val leftWidth = callExpression.receiver!!.type.asBitWidthOrNull(callExpression)
                ?: return
            val rightWidth = callExpression.valueArguments[0].type.asBitWidthOrNull(callExpression)
                ?: return
            val type = Core.Vk.UBIT.toType(CoreCardinalConstantDeclaration(max(leftWidth, rightWidth)).toType())
            callExpression.type = type
        }
    }
}
