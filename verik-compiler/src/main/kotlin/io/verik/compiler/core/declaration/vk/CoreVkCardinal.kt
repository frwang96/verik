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

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.core.common.CoreCardinalFunctionDeclaration
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope

object CoreVkCardinal : CoreScope(CorePackage.VK) {

    val N_ADD = CoreCardinalFunctionDeclaration(parent, "ADD")
    val N_SUB = CoreCardinalFunctionDeclaration(parent, "SUB")
    val N_MUL = CoreCardinalFunctionDeclaration(parent, "MUL")
    val N_DIV = CoreCardinalFunctionDeclaration(parent, "DIV")
    val N_MAX = CoreCardinalFunctionDeclaration(parent, "MAX")
    val N_MIN = CoreCardinalFunctionDeclaration(parent, "MIN")
    val N_OF = CoreCardinalFunctionDeclaration(parent, "OF")
    val N_INC = CoreCardinalFunctionDeclaration(parent, "INC")
    val N_DEC = CoreCardinalFunctionDeclaration(parent, "DEC")
    val N_LOG = CoreCardinalFunctionDeclaration(parent, "LOG")
    val N_WIDTH = CoreCardinalFunctionDeclaration(parent, "WIDTH")
    val N_EXP = CoreCardinalFunctionDeclaration(parent, "EXP")
}
