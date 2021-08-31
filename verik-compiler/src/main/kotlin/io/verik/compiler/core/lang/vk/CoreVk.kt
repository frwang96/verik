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

import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreCardinalFunctionDeclaration
import io.verik.compiler.core.common.CoreKtFunctionDeclaration
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope

object CoreVk : CoreScope(CorePackage.VK) {

    val ADD = CoreCardinalFunctionDeclaration("ADD")
    val INC = CoreCardinalFunctionDeclaration("INC")
    val MAX = CoreCardinalFunctionDeclaration("MAX")

    val U_INT = CoreKtFunctionDeclaration(parent, "u", Core.Kt.INT)

    val RANDOM = CoreKtFunctionDeclaration(parent, "random")
    val RANDOM_INT = CoreKtFunctionDeclaration(parent, "random", Core.Kt.INT)
    val FOREVER_FUNCTION = CoreKtFunctionDeclaration(parent, "forever", Core.Kt.FUNCTION)
    val ON_EVENT_FUNCTION = CoreKtFunctionDeclaration(parent, "on", Core.Vk.EVENT, Core.Kt.FUNCTION)

    val POSEDGE_BOOLEAN = CoreKtFunctionDeclaration(parent, "posedge", Core.Kt.BOOLEAN)
    val NEGEDGE_BOOLEAN = CoreKtFunctionDeclaration(parent, "negedge", Core.Kt.BOOLEAN)
    val WAIT_EVENT = CoreKtFunctionDeclaration(parent, "wait", Core.Vk.EVENT)
    val DELAY_INT = CoreKtFunctionDeclaration(parent, "delay", Core.Kt.INT)
    val FINISH = CoreKtFunctionDeclaration(parent, "finish")

    val SV_STRING = CoreKtFunctionDeclaration(parent, "sv", Core.Kt.STRING)
}
