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
import io.verik.compiler.core.vk.CoreVkSbit
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

        object Int : CoreScope(C_Int) {

            val F_times_Int = CoreKtInt.F_times_Int
            val F_plus_Int = CoreKtInt.F_plus_Int
            val F_minus_Int = CoreKtInt.F_minus_Int
            val F_lt_Int = CoreKtInt.F_lt_Int
            val F_lteq_Int = CoreKtInt.F_lteq_Int
            val F_gt_Int = CoreKtInt.F_gt_Int
            val F_gteq_Int = CoreKtInt.F_gteq_Int
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

    object Jv : CoreScope(CorePackage.JV) {

        object Util : CoreScope(CorePackage.JV_UTIL) {

            val C_ArrayList = CoreClass.Jv.Util.C_ArrayList

            val F_ArrayList = CoreConstructorDeclaration(C_ArrayList)
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
        val C_ModuleInterface = CoreClass.Vk.C_ModuleInterface
        val C_ModulePort = CoreClass.Vk.C_ModulePort
        val C_ClockingBlock = CoreClass.Vk.C_ClockingBlock
        val C_Time = CoreClass.Vk.C_Time
        val C_Event = CoreClass.Vk.C_Event

        val F_Struct = CoreConstructorDeclaration(C_Struct)
        val F_Module = CoreConstructorDeclaration(C_Module)
        val F_ModuleInterface = CoreConstructorDeclaration(C_ModuleInterface)
        val F_ModulePort = CoreConstructorDeclaration(C_ModulePort)
        val F_ClockingBlock = CoreConstructorDeclaration(C_ClockingBlock)

        val N_Cardinal = CoreCardinalUnresolvedDeclaration
        val N_ADD = CoreVkCardinal.N_ADD
        val N_SUB = CoreVkCardinal.N_SUB
        val N_MUL = CoreVkCardinal.N_MUL
        val N_MAX = CoreVkCardinal.N_MAX
        val N_MIN = CoreVkCardinal.N_MIN
        val N_ID = CoreVkCardinal.N_ID
        val N_INC = CoreVkCardinal.N_INC
        val N_DEC = CoreVkCardinal.N_DEC
        val N_LOG = CoreVkCardinal.N_LOG
        val N_WIDTH = CoreVkCardinal.N_WIDTH
        val N_EXP = CoreVkCardinal.N_EXP

        val F_nc = CoreVk.F_nc
        val F_i = CoreVk.F_i
        val F_u = CoreVk.F_u
        val F_u_Int = CoreVk.F_u_Int
        val F_u_String = CoreVk.F_u_String
        val F_u_Sbit = CoreVk.F_u_Sbit
        val F_u0 = CoreVk.F_u0
        val F_s_Ubit = CoreVk.F_s_Ubit
        val F_cat = CoreVk.F_cat
        val F_rep = CoreVk.F_rep
        val F_random = CoreVk.F_random
        val F_random_Int = CoreVk.F_random_Int
        val F_random_Int_Int = CoreVk.F_random_Int_Int
        val F_randomBoolean = CoreVk.F_randomBoolean
        val F_randomUbit = CoreVk.F_randomUbit
        val F_forever_Function = CoreVk.F_forever_Function
        val F_on_Event_Function = CoreVk.F_on_Event_Function
        val F_posedge_Boolean = CoreVk.F_posedge_Boolean
        val F_negedge_Boolean = CoreVk.F_negedge_Boolean
        val F_wait_Boolean = CoreVk.F_wait_Boolean
        val F_wait_Event = CoreVk.F_wait_Event
        val F_wait_ClockingBlock = CoreVk.F_wait_ClockingBlock
        val F_delay_Int = CoreVk.F_delay_Int
        val F_time = CoreVk.F_time
        val F_finish = CoreVk.F_finish
        val F_fatal = CoreVk.F_fatal
        val F_sv_String = CoreVk.F_sv_String

        val F_Boolean_uext = CoreVk.F_Boolean_uext
        val F_Boolean_sext = CoreVk.F_Boolean_sext

        object Ubit : CoreScope(C_Ubit) {

            val F_get_Int = CoreVkUbit.F_get_Int
            val F_set_Int_Boolean = CoreVkUbit.F_set_Int_Boolean
            val F_set_Int_Ubit = CoreVkUbit.F_set_Int_Ubit
            val F_unaryMinus = CoreVkUbit.F_unaryMinus
            val F_plus_Ubit = CoreVkUbit.F_plus_Ubit
            val F_add_Ubit = CoreVkUbit.F_add_Ubit
            val F_minus_Ubit = CoreVkUbit.F_minus_Ubit
            val F_times_Ubit = CoreVkUbit.F_times_Ubit
            val F_mul_Ubit = CoreVkUbit.F_mul_Ubit
            val F_div_Ubit = CoreVkUbit.F_div_Ubit
            val F_and_Ubit = CoreVkUbit.F_and_Ubit
            val F_or_Ubit = CoreVkUbit.F_or_Ubit
            val F_xor_Ubit = CoreVkUbit.F_xor_Ubit
            val F_sll_Int = CoreVkUbit.F_sll_Int
            val F_sll_Ubit = CoreVkUbit.F_sll_Ubit
            val F_srl_Int = CoreVkUbit.F_srl_Int
            val F_srl_Ubit = CoreVkUbit.F_srl_Ubit
            val F_sra_Int = CoreVkUbit.F_sra_Int
            val F_sra_Ubit = CoreVkUbit.F_sra_Ubit
            val F_lt_Ubit = CoreVkUbit.F_lt_Ubit
            val F_lteq_Ubit = CoreVkUbit.F_lteq_Ubit
            val F_gt_Ubit = CoreVkUbit.F_gt_Ubit
            val F_gteq_Ubit = CoreVkUbit.F_gteq_Ubit
            val F_invert = CoreVkUbit.F_invert
            val F_reverse = CoreVkUbit.F_reverse
            val F_uext = CoreVkUbit.F_uext
            val F_sext = CoreVkUbit.F_sext
            val F_tru = CoreVkUbit.F_tru
            val F_slice_Int = CoreVkUbit.F_slice_Int
        }

        object Sbit : CoreScope(C_Sbit) {

            val F_lt_Sbit = CoreVkSbit.F_lt_Sbit
        }

        object Unpacked : CoreScope(C_Unpacked) {

            val F_get_Int = CoreVkUnpacked.F_get_Int
            val F_set_Int_Any = CoreVkUnpacked.F_set_Int_Any
            val F_get_Ubit = CoreVkUnpacked.F_get_Ubit
            val F_set_Ubit_Any = CoreVkUnpacked.F_set_Ubit_Any
            val F_sort = CoreVkUnpacked.F_sort

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
        val F_unsigned = CoreSv.F_unsigned
        val F_signed = CoreSv.F_signed
        val F_time = CoreSv.F_time
        val F_fatal = CoreSv.F_fatal
        val F_finish = CoreSv.F_finish
        val F_new = CoreSv.F_new
        val F_wait = CoreSv.F_wait
        val F_name = CoreSv.F_name
        val F_rsort = CoreSv.F_rsort
    }
}
