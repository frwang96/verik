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

package io.verik.compiler.core.vk

import io.verik.compiler.core.common.CoreCardinalFunctionDeclaration

object CoreVkCardinal {

    val N_ADD = CoreCardinalFunctionDeclaration("ADD")
    val N_SUB = CoreCardinalFunctionDeclaration("SUB")
    val N_MUL = CoreCardinalFunctionDeclaration("MUL")
    val N_MAX = CoreCardinalFunctionDeclaration("MAX")
    val N_MIN = CoreCardinalFunctionDeclaration("MIN")
    val N_INC = CoreCardinalFunctionDeclaration("INC")
    val N_DEC = CoreCardinalFunctionDeclaration("DEC")
    val N_LOG = CoreCardinalFunctionDeclaration("LOG")
    val N_WIDTH = CoreCardinalFunctionDeclaration("WIDTH")
    val N_EXP = CoreCardinalFunctionDeclaration("EXP")
}
