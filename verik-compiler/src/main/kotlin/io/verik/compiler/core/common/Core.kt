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

@file:Suppress("unused")

package io.verik.compiler.core.common

import io.verik.compiler.core.kt.CoreKtBoolean
import io.verik.compiler.core.kt.CoreKtCollections
import io.verik.compiler.core.kt.CoreKtInt
import io.verik.compiler.core.kt.CoreKtIo
import io.verik.compiler.core.sv.CoreSv
import io.verik.compiler.core.vk.CoreVk
import io.verik.compiler.core.vk.CoreVkCardinal
import io.verik.compiler.core.vk.CoreVkUbit
import io.verik.compiler.core.vk.CoreVkUnpacked

object Core {

    object Kt : CoreScope(CorePackage.KT) {

        val C_ANY = CoreClass.Kt.C_ANY
        val C_NOTHING = CoreClass.Kt.C_NOTHING
        val C_FUNCTION = CoreClass.Kt.C_FUNCTION
        val C_UNIT = CoreClass.Kt.C_UNIT
        val C_INT = CoreClass.Kt.C_INT
        val C_BOOLEAN = CoreClass.Kt.C_BOOLEAN
        val C_STRING = CoreClass.Kt.C_STRING
        val C_ENUM = CoreClass.Kt.C_ENUM

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

        object Collections : CoreScope(CorePackage.KT_COLLECTIONS) {

            val F_FOR_EACH_FUNCTION = CoreKtCollections.F_FOR_EACH_FUNCTION
        }
    }

    object Vk : CoreScope(CorePackage.VK) {

        fun cardinalOf(value: Int): CoreCardinalConstantDeclaration {
            return CoreCardinalConstantDeclaration(value)
        }

        val C_UBIT = CoreClass.Vk.C_UBIT
        val C_SBIT = CoreClass.Vk.C_SBIT
        val C_STRUCT = CoreClass.Vk.C_STRUCT
        val C_PACKED = CoreClass.Vk.C_PACKED
        val C_UNPACKED = CoreClass.Vk.C_UNPACKED
        val C_COMPONENT = CoreClass.Vk.C_COMPONENT
        val C_MODULE = CoreClass.Vk.C_MODULE
        val C_TIME = CoreClass.Vk.C_TIME
        val C_EVENT = CoreClass.Vk.C_EVENT

        val N_CARDINAL = CoreCardinalUnresolvedDeclaration
        val N_ADD = CoreVkCardinal.N_ADD
        val N_SUB = CoreVkCardinal.N_SUB
        val N_MUL = CoreVkCardinal.N_MUL
        val N_MAX = CoreVkCardinal.N_MAX
        val N_MIN = CoreVkCardinal.N_MIN
        val N_INC = CoreVkCardinal.N_INC
        val N_DEC = CoreVkCardinal.N_DEC
        val N_LOG = CoreVkCardinal.N_LOG
        val N_INCLOG = CoreVkCardinal.N_INCLOG
        val N_EXP = CoreVkCardinal.N_EXP

        val F_NC = CoreVk.F_NC
        val F_U = CoreVk.F_U
        val F_U_INT = CoreVk.F_U_INT
        val F_ZEROES = CoreVk.F_ZEROES
        val F_CAT = CoreVk.F_CAT
        val F_RANDOM = CoreVk.F_RANDOM
        val F_RANDOM_INT = CoreVk.F_RANDOM_INT
        val F_RANDOM_UBIT = CoreVk.F_RANDOM_UBIT
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
            val F_GET_INT = CoreVkUbit.F_GET_INT
            val F_SET_INT_BOOLEAN = CoreVkUbit.F_SET_INT_BOOLEAN
            val F_PLUS_UBIT = CoreVkUbit.F_PLUS_UBIT
            val F_ADD_UBIT = CoreVkUbit.F_ADD_UBIT
            val F_MUL_UBIT = CoreVkUbit.F_MUL_UBIT
            val F_SHL_INT = CoreVkUbit.F_SHL_INT
            val F_SHR_INT = CoreVkUbit.F_SHR_INT
            val F_EXT = CoreVkUbit.F_EXT
            val F_SLICE_INT = CoreVkUbit.F_SLICE_INT
        }

        object Unpacked : CoreScope(C_UNPACKED) {

            val F_GET_INT = CoreVkUnpacked.F_GET_INT
            val F_SET_INT_ANY = CoreVkUnpacked.F_SET_INT_ANY
        }
    }

    object Sv : CoreScope(CorePackage.SV) {

        val F_DISPLAY = CoreSv.F_DISPLAY
        val F_WRITE = CoreSv.F_WRITE
        val F_SFORMATF = CoreSv.F_SFORMATF
        val F_RANDOM = CoreSv.F_RANDOM
        val F_URANDOM = CoreSv.F_URANDOM
        val F_TIME = CoreSv.F_TIME
        val F_FINISH = CoreSv.F_FINISH
        val F_NEW = CoreSv.F_NEW
    }
}
