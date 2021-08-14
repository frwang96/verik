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

package io.verik.compiler.core.common

import io.verik.compiler.core.lang.kt.CoreKtBoolean
import io.verik.compiler.core.lang.kt.CoreKtClass
import io.verik.compiler.core.lang.kt.CoreKtInt
import io.verik.compiler.core.lang.kt.CoreKtIo
import io.verik.compiler.core.lang.sv.CoreSv
import io.verik.compiler.core.lang.vk.CoreVk
import io.verik.compiler.core.lang.vk.CoreVkClass
import io.verik.compiler.core.lang.vk.CoreVkUbit

object Core {

    object Kt : CoreScope(CorePackage.KT) {

        val ANY = CoreKtClass.ANY
        val NOTHING = CoreKtClass.NOTHING
        val FUNCTION = CoreKtClass.FUNCTION
        val UNIT = CoreKtClass.UNIT
        val INT = CoreKtClass.INT
        val BOOLEAN = CoreKtClass.BOOLEAN
        val STRING = CoreKtClass.STRING
        val ENUM = CoreKtClass.ENUM

        object Int : CoreScope(INT) {

            val TIMES_INT = CoreKtInt.TIMES_INT
            val PLUS_INT = CoreKtInt.PLUS_INT
            val MINUS_INT = CoreKtInt.MINUS_INT
        }

        object Boolean : CoreScope(BOOLEAN) {

            val NOT = CoreKtBoolean.NOT
        }

        object Io : CoreScope(CorePackage.KT_IO) {

            val PRINT = CoreKtIo.PRINT
            val PRINT_ANY = CoreKtIo.PRINT_ANY
            val PRINT_INT = CoreKtIo.PRINT_INT
            val PRINTLN = CoreKtIo.PRINTLN
            val PRINTLN_ANY = CoreKtIo.PRINTLN_ANY
            val PRINTLN_INT = CoreKtIo.PRINTLN_INT
        }
    }

    object Vk : CoreScope(CorePackage.VK) {

        val UBIT = CoreVkClass.UBIT
        val SBIT = CoreVkClass.SBIT
        val CLASS = CoreVkClass.CLASS
        val EVENT = CoreVkClass.EVENT
        val COMPONENT = CoreVkClass.COMPONENT
        val MODULE = CoreVkClass.MODULE

        val CARDINAL = CoreCardinalBaseDeclaration
        val ADD = CoreVk.ADD
        val INC = CoreVk.INC

        val U_INT = CoreVk.U_INT
        val RANDOM = CoreVk.RANDOM
        val RANDOM_INT = CoreVk.RANDOM_INT
        val FOREVER_FUNCTION = CoreVk.FOREVER_FUNCTION
        val ON_EVENT_FUNCTION = CoreVk.ON_EVENT_FUNCTION
        val POSEDGE_BOOLEAN = CoreVk.POSEDGE_BOOLEAN
        val NEGEDGE_BOOLEAN = CoreVk.NEGEDGE_BOOLEAN
        val WAIT_EVENT = CoreVk.WAIT_EVENT
        val DELAY_INT = CoreVk.DELAY_INT
        val FINISH = CoreVk.FINISH
        val SV_STRING = CoreVk.SV_STRING

        object Ubit : CoreScope(UBIT) {

            val INV = CoreVkUbit.INV
            val PLUS_UBIT = CoreVkUbit.PLUS_UBIT
        }
    }

    object Sv : CoreScope(CorePackage.SV) {

        val DISPLAY = CoreSv.DISPLAY
        val WRITE = CoreSv.WRITE
        val SFORMATF = CoreSv.SFORMATF
        val RANDOM = CoreSv.RANDOM
        val FINISH = CoreSv.FINISH
    }
}