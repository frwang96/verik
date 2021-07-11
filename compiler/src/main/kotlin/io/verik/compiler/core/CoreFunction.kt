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

            val TIMES_INT = CoreKtFunctionDeclaration(parent, "times", CoreClass.Kotlin.INT)
            val PLUS_INT = CoreKtFunctionDeclaration(parent, "plus", CoreClass.Kotlin.INT)
            val MINUS_INT = CoreKtFunctionDeclaration(parent, "minus", CoreClass.Kotlin.INT)
        }
    }

    object KotlinIo : CoreScope(CorePackage.KOTLIN_IO) {

        val PRINT = CoreKtFunctionDeclaration(parent, "print")
        val PRINT_ANY = CoreKtFunctionDeclaration(parent, "print", CoreClass.Kotlin.ANY)
        val PRINT_INT = CoreKtFunctionDeclaration(parent, "print", CoreClass.Kotlin.INT)

        val PRINTLN = CoreKtFunctionDeclaration(parent, "println")
        val PRINTLN_ANY = CoreKtFunctionDeclaration(parent, "println", CoreClass.Kotlin.ANY)
        val PRINTLN_INT = CoreKtFunctionDeclaration(parent, "println", CoreClass.Kotlin.INT)
    }

    object Core : CoreScope(CorePackage.VERIK_CORE) {

        val U_INT = CoreKtFunctionDeclaration(parent, "u", CoreClass.Kotlin.INT)
        val RANDOM = CoreKtFunctionDeclaration(parent, "random")
        val RANDOM_INT = CoreKtFunctionDeclaration(parent, "random", CoreClass.Kotlin.INT)
        val FOREVER_FUNCTION = CoreKtFunctionDeclaration(parent, "forever", CoreClass.Kotlin.FUNCTION)
        val ON_EVENT_FUNCTION = CoreKtFunctionDeclaration(parent, "on", CoreClass.Core.EVENT, CoreClass.Kotlin.FUNCTION)

        val POSEDGE_BOOLEAN = CoreKtFunctionDeclaration(parent, "posedge", CoreClass.Kotlin.BOOLEAN)
        val NEGEDGE_BOOLEAN = CoreKtFunctionDeclaration(parent, "negedge", CoreClass.Kotlin.BOOLEAN)
        val WAIT_EVENT = CoreKtFunctionDeclaration(parent, "wait", CoreClass.Core.EVENT)

        object Ubit : CoreScope(CoreClass.Core.UBIT) {

            val INV = CoreKtFunctionDeclaration(parent, "inv")
        }
    }

    object Sv : CoreScope(CorePackage.VERIK_SV) {

        val DISPLAY = CoreSvFunctionDeclaration(parent, "\$display")
        val WRITE = CoreSvFunctionDeclaration(parent, "\$write")
        val SFORMATF = CoreSvFunctionDeclaration(parent, "\$sformatf")
        val RANDOM = CoreSvFunctionDeclaration(parent, "\$random")
    }
}