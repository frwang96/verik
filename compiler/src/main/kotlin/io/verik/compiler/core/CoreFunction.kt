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

package io.verik.compiler.core

object CoreFunction {

    object Kotlin : CoreScope(CorePackage.KOTLIN) {

        object Int : CoreScope(CoreClass.Kotlin.INT) {

            val PLUS_INT = CoreKtFunctionDeclaration(parent, "plus", CoreClass.Kotlin.INT)
        }
    }

    object KotlinIo : CoreScope(CorePackage.KOTLIN_IO) {

        val PRINTLN = CoreKtFunctionDeclaration(parent, "println")
    }

    object Core : CoreScope(CorePackage.VERIK_CORE) {

        val U_INT = CoreKtFunctionDeclaration(parent, "u", CoreClass.Kotlin.INT)
        val RANDOM = CoreKtFunctionDeclaration(parent, "random")
        val RANDOM_INT = CoreKtFunctionDeclaration(parent, "random", CoreClass.Kotlin.INT)

        object Ubit : CoreScope(CoreClass.Core.UBIT) {

            val INV = CoreKtFunctionDeclaration(parent, "inv")
        }
    }

    object Sv : CoreScope(CorePackage.VERIK_SV) {

        val DISPLAY = CoreSvFunctionDeclaration(parent, "\$display")
        val RANDOM = CoreSvFunctionDeclaration(parent, "\$random")
    }
}