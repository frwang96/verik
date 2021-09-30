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

import io.verik.compiler.core.kt.CoreKt
import io.verik.compiler.core.kt.CoreKtBoolean
import io.verik.compiler.core.kt.CoreKtCollections
import io.verik.compiler.core.kt.CoreKtInt
import io.verik.compiler.core.kt.CoreKtIo
import io.verik.compiler.core.kt.CoreKtRanges
import io.verik.compiler.core.sv.CoreSv
import io.verik.compiler.core.vk.CoreVk
import io.verik.compiler.core.vk.CoreVkCardinal
import io.verik.compiler.core.vk.CoreVkUbit
import io.verik.compiler.core.vk.CoreVkUnpacked

object Core {

    object Kt : CoreScope(CorePackage.KT) {

        val C_Any = CoreClass.Kt.C_Any
        val C_Nothing = CoreClass.Kt.C_Nothing
        val C_Function = CoreClass.Kt.C_Function
        val C_Unit = CoreClass.Kt.C_Unit
        val C_Int = CoreClass.Kt.C_Int
        val C_Boolean = CoreClass.Kt.C_Boolean
        val C_String = CoreClass.Kt.C_String
        val C_Enum = CoreClass.Kt.C_Enum

        val F_repeat_Int_Function = CoreKt.F_repeat_Int_Function
        val F_TODO = CoreKt.F_TODO

        object Int : CoreScope(C_Int) {

            val F_times_Int = CoreKtInt.F_times_Int
            val F_plus_Int = CoreKtInt.F_plus_Int
            val F_minus_Int = CoreKtInt.F_minus_Int
        }

        object Boolean : CoreScope(C_Boolean) {

            val F_not = CoreKtBoolean.F_not
            val F_and_Boolean = CoreKtBoolean.F_and_Boolean
            val F_or_Boolean = CoreKtBoolean.F_or_Boolean
            val F_xor_Boolean = CoreKtBoolean.F_xor_Boolean
        }

        object Io : CoreScope(CorePackage.KT_IO) {

            val F_print = CoreKtIo.F_print
            val F_print_Any = CoreKtIo.F_print_Any
            val F_print_Boolean = CoreKtIo.F_print_Boolean
            val F_print_Int = CoreKtIo.F_print_Int
            val F_println = CoreKtIo.F_println
            val F_println_Any = CoreKtIo.F_println_Any
            val F_println_Boolean = CoreKtIo.F_println_Boolean
            val F_println_Int = CoreKtIo.F_println_Int
        }

        object Collections : CoreScope(CorePackage.KT_COLLECTIONS) {

            val F_forEach_Function = CoreKtCollections.F_forEach_Function
        }

        object Ranges : CoreScope(CorePackage.Kt_RANGES) {

            val C_IntRange = CoreClass.Kt.Ranges.C_IntRange

            val F_until_Int = CoreKtRanges.F_until_Int
        }
    }

    object Vk : CoreScope(CorePackage.VK) {

        fun cardinalOf(value: Int): CoreCardinalConstantDeclaration {
            return CoreCardinalConstantDeclaration(value)
        }

        val C_Ubit = CoreClass.Vk.C_Ubit
        val C_Sbit = CoreClass.Vk.C_Sbit
        val C_Struct = CoreClass.Vk.C_Struct
        val C_Packed = CoreClass.Vk.C_Packed
        val C_Unpacked = CoreClass.Vk.C_Unpacked
        val C_Component = CoreClass.Vk.C_Component
        val C_Module = CoreClass.Vk.C_Module
        val C_Interface = CoreClass.Vk.C_Interface
        val C_Modport = CoreClass.Vk.C_Modport
        val C_ClockingBlock = CoreClass.Vk.C_ClockingBlock
        val C_Time = CoreClass.Vk.C_Time
        val C_Event = CoreClass.Vk.C_Event

        val N_Cardinal = CoreCardinalUnresolvedDeclaration
        val N_ADD = CoreVkCardinal.N_ADD
        val N_SUB = CoreVkCardinal.N_SUB
        val N_MUL = CoreVkCardinal.N_MUL
        val N_MAX = CoreVkCardinal.N_MAX
        val N_MIN = CoreVkCardinal.N_MIN
        val N_INC = CoreVkCardinal.N_INC
        val N_DEC = CoreVkCardinal.N_DEC
        val N_LOG = CoreVkCardinal.N_LOG
        val N_WIDTH = CoreVkCardinal.N_WIDTH
        val N_EXP = CoreVkCardinal.N_EXP

        val F_nc = CoreVk.F_nc
        val F_i = CoreVk.F_i
        val F_u = CoreVk.F_u
        val F_u_Int = CoreVk.F_u_Int
        val F_zeroes = CoreVk.F_zeroes
        val F_cat = CoreVk.F_cat
        val F_random = CoreVk.F_random
        val F_random_Int = CoreVk.F_random_Int
        val F_random_Int_Int = CoreVk.F_random_Int_Int
        val F_random_Ubit = CoreVk.F_random_Ubit
        val F_forever_Function = CoreVk.F_forever_Function
        val F_on_Event_Function = CoreVk.F_on_Event_Function
        val F_posedge_Boolean = CoreVk.F_posedge_Boolean
        val F_negedge_Boolean = CoreVk.F_negedge_Boolean
        val F_wait_Event = CoreVk.F_wait_Event
        val F_wait_ClockingBlock = CoreVk.F_wait_ClockingBlock
        val F_delay_Int = CoreVk.F_delay_Int
        val F_time = CoreVk.F_time
        val F_finish = CoreVk.F_finish
        val F_sv_String = CoreVk.F_sv_String

        object Ubit : CoreScope(C_Ubit) {

            val F_inv = CoreVkUbit.F_inv
            val F_get_Int = CoreVkUbit.F_get_Int
            val F_set_Int_Boolean = CoreVkUbit.F_set_Int_Boolean
            val F_plus_Ubit = CoreVkUbit.F_plus_Ubit
            val F_add_Ubit = CoreVkUbit.F_add_Ubit
            val F_mul_Ubit = CoreVkUbit.F_mul_Ubit
            val F_shl_Int = CoreVkUbit.F_shl_Int
            val F_shr_Int = CoreVkUbit.F_shr_Int
            val F_ext = CoreVkUbit.F_ext
            val F_slice_Int = CoreVkUbit.F_slice_Int
        }

        object Unpacked : CoreScope(C_Unpacked) {

            val F_get_Int = CoreVkUnpacked.F_get_Int
            val F_set_Int_Any = CoreVkUnpacked.F_set_Int_Any
            val F_get_Ubit = CoreVkUnpacked.F_get_Ubit
            val F_set_Ubit_Any = CoreVkUnpacked.F_set_Ubit_Any

            val P_size = CoreVkUnpacked.P_size
        }
    }

    object Sv : CoreScope(CorePackage.SV) {

        val F_display = CoreSv.F_display
        val F_write = CoreSv.F_write
        val F_sformatf = CoreSv.F_sformatf
        val F_random = CoreSv.F_random
        val F_urandom = CoreSv.F_urandom
        val F_urandom_range = CoreSv.F_urandom_range
        val F_time = CoreSv.F_time
        val F_fatal = CoreSv.F_fatal
        val F_finish = CoreSv.F_finish
        val F_new = CoreSv.F_new
        val F_name = CoreSv.F_name
    }
}
