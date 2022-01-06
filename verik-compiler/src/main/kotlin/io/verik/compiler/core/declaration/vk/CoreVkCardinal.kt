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

    val T_ADD = CoreCardinalFunctionDeclaration(parent, "ADD")
    val T_SUB = CoreCardinalFunctionDeclaration(parent, "SUB")
    val T_MUL = CoreCardinalFunctionDeclaration(parent, "MUL")
    val T_DIV = CoreCardinalFunctionDeclaration(parent, "DIV")
    val T_MAX = CoreCardinalFunctionDeclaration(parent, "MAX")
    val T_MIN = CoreCardinalFunctionDeclaration(parent, "MIN")
    val T_OF = CoreCardinalFunctionDeclaration(parent, "OF")
    val T_INC = CoreCardinalFunctionDeclaration(parent, "INC")
    val T_DEC = CoreCardinalFunctionDeclaration(parent, "DEC")
    val T_LOG = CoreCardinalFunctionDeclaration(parent, "LOG")
    val T_WIDTH = CoreCardinalFunctionDeclaration(parent, "WIDTH")
    val T_EXP = CoreCardinalFunctionDeclaration(parent, "EXP")
    val T_IF = CoreCardinalFunctionDeclaration(parent, "IF")
}
