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

        val C_ANY = CoreKtClass.C_ANY
        val C_NOTHING = CoreKtClass.C_NOTHING
        val C_FUNCTION = CoreKtClass.C_FUNCTION
        val C_UNIT = CoreKtClass.C_UNIT
        val C_INT = CoreKtClass.C_INT
        val C_BOOLEAN = CoreKtClass.C_BOOLEAN
        val C_STRING = CoreKtClass.C_STRING
        val C_ENUM = CoreKtClass.C_ENUM

        object Int : CoreScope(C_INT) {

            val F_TIMES_INT = CoreKtInt.F_TIMES_INT
            val F_PLUS_INT = CoreKtInt.F_PLUS_INT
            val F_MINUS_INT = CoreKtInt.F_MINUS_INT
        }

        object Boolean : CoreScope(C_BOOLEAN) {

            val F_NOT = CoreKtBoolean.F_NOT
        }

        object Io : CoreScope(CorePackage.KT_IO) {

            val F_PRINT = CoreKtIo.F_PRINT
            val F_PRINT_ANY = CoreKtIo.F_PRINT_ANY
            val F_PRINT_INT = CoreKtIo.F_PRINT_INT
            val F_PRINTLN = CoreKtIo.F_PRINTLN
            val F_PRINTLN_ANY = CoreKtIo.F_PRINTLN_ANY
            val F_PRINTLN_INT = CoreKtIo.F_PRINTLN_INT
        }
    }

    object Vk : CoreScope(CorePackage.VK) {

        fun cardinalOf(value: Int): CoreCardinalConstantDeclaration {
            return CoreCardinalConstantDeclaration(value)
        }

        val C_UBIT = CoreVkClass.C_UBIT
        val C_SBIT = CoreVkClass.C_SBIT
        val C_STRUCT = CoreVkClass.C_STRUCT
        val C_COMPONENT = CoreVkClass.C_COMPONENT
        val C_MODULE = CoreVkClass.C_MODULE
        val C_TIME = CoreVkClass.C_TIME
        val C_EVENT = CoreVkClass.C_EVENT

        val N_CARDINAL = CoreCardinalBaseDeclaration
        val N_ADD = CoreVk.N_ADD
        val N_INC = CoreVk.N_INC
        val N_MAX = CoreVk.N_MAX

        val F_NC = CoreVk.F_NC
        val F_U_INT = CoreVk.F_U_INT
        val F_ZEROES = CoreVk.F_ZEROES
        val F_RANDOM = CoreVk.F_RANDOM
        val F_RANDOM_INT = CoreVk.F_RANDOM_INT
        val F_FOREVER_FUNCTION = CoreVk.F_FOREVER_FUNCTION
        val F_ON_EVENT_FUNCTION = CoreVk.F_ON_EVENT_FUNCTION
        val F_POSEDGE_BOOLEAN = CoreVk.F_POSEDGE_BOOLEAN
        val F_NEGEDGE_BOOLEAN = CoreVk.F_NEGEDGE_BOOLEAN
        val F_WAIT_EVENT = CoreVk.F_WAIT_EVENT
        val F_DELAY_INT = CoreVk.F_DELAY_INT
        val F_TIME = CoreVk.F_TIME
        val F_FINISH = CoreVk.F_FINISH
        val F_SV_STRING = CoreVk.F_SV_STRING

        object Ubit : CoreScope(C_UBIT) {

            val F_INV = CoreVkUbit.F_INV
            val F_PLUS_UBIT = CoreVkUbit.F_PLUS_UBIT
            val F_SHL_INT = CoreVkUbit.F_SHL_INT
            val F_SHR_INT = CoreVkUbit.F_SHR_INT
            val F_EXT = CoreVkUbit.F_EXT
        }
    }

    object Sv : CoreScope(CorePackage.SV) {

        val F_DISPLAY = CoreSv.F_DISPLAY
        val F_WRITE = CoreSv.F_WRITE
        val F_SFORMATF = CoreSv.F_SFORMATF
        val F_RANDOM = CoreSv.F_RANDOM
        val F_TIME = CoreSv.F_TIME
        val F_FINISH = CoreSv.F_FINISH
        val F_NEW = CoreSv.F_NEW
    }
}
