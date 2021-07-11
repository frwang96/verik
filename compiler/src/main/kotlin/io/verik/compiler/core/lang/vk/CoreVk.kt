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

import io.verik.compiler.ast.property.Name
import io.verik.compiler.core.common.*

object CoreVk : CoreScope(CorePackage.VK) {

    val ADD = CoreCardinalFunctionDeclaration(Name("ADD"))
    val INC = CoreCardinalFunctionDeclaration(Name("INC"))

    val U_INT = CoreKtFunctionDeclaration(parent, "u", C.Kt.INT)
    val RANDOM = CoreKtFunctionDeclaration(parent, "random")
    val RANDOM_INT = CoreKtFunctionDeclaration(parent, "random", C.Kt.INT)
    val FOREVER_FUNCTION = CoreKtFunctionDeclaration(parent, "forever", C.Kt.FUNCTION)
    val ON_EVENT_FUNCTION = CoreKtFunctionDeclaration(parent, "on", C.Vk.EVENT, C.Kt.FUNCTION)

    val POSEDGE_BOOLEAN = CoreKtFunctionDeclaration(parent, "posedge", C.Kt.BOOLEAN)
    val NEGEDGE_BOOLEAN = CoreKtFunctionDeclaration(parent, "negedge", C.Kt.BOOLEAN)
    val WAIT_EVENT = CoreKtFunctionDeclaration(parent, "wait", C.Vk.EVENT)
}