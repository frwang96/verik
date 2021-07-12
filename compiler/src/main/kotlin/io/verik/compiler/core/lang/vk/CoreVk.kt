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

import io.verik.compiler.ast.element.common.ECallExpression
import io.verik.compiler.ast.property.Name
import io.verik.compiler.core.common.*

object CoreVk : CoreScope(CorePackage.VK) {

    val ADD = CoreCardinalFunctionDeclaration(Name("ADD"))
    val INC = CoreCardinalFunctionDeclaration(Name("INC"))

    val U_INT = object : CoreKtFunctionDeclaration(parent, "u", Core.Kt.INT) {

        override fun resolve(callExpression: ECallExpression) {
            if (callExpression.hasTypeArguments()) {
                callExpression.type = Core.Vk.UBIT.toType(callExpression.typeArguments[0].type)
            }
        }
    }

    val RANDOM = CoreKtFunctionDeclaration(parent, "random")
    val RANDOM_INT = CoreKtFunctionDeclaration(parent, "random", Core.Kt.INT)
    val FOREVER_FUNCTION = CoreKtFunctionDeclaration(parent, "forever", Core.Kt.FUNCTION)
    val ON_EVENT_FUNCTION = CoreKtFunctionDeclaration(parent, "on", Core.Vk.EVENT, Core.Kt.FUNCTION)

    val POSEDGE_BOOLEAN = CoreKtFunctionDeclaration(parent, "posedge", Core.Kt.BOOLEAN)
    val NEGEDGE_BOOLEAN = CoreKtFunctionDeclaration(parent, "negedge", Core.Kt.BOOLEAN)
    val WAIT_EVENT = CoreKtFunctionDeclaration(parent, "wait", Core.Vk.EVENT)
}