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

@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package io.verik.compiler.core.common

import io.verik.compiler.core.declaration.jv.CoreJvArrayList
import io.verik.compiler.core.declaration.jv.CoreJvClass
import io.verik.compiler.core.declaration.kt.CoreKt
import io.verik.compiler.core.declaration.kt.CoreKtBoolean
import io.verik.compiler.core.declaration.kt.CoreKtClass
import io.verik.compiler.core.declaration.kt.CoreKtCollections
import io.verik.compiler.core.declaration.kt.CoreKtInt
import io.verik.compiler.core.declaration.kt.CoreKtIo
import io.verik.compiler.core.declaration.kt.CoreKtRanges
import io.verik.compiler.core.declaration.vk.CoreVk
import io.verik.compiler.core.declaration.vk.CoreVkCardinal
import io.verik.compiler.core.declaration.vk.CoreVkClass
import io.verik.compiler.core.declaration.vk.CoreVkSbit
import io.verik.compiler.core.declaration.vk.CoreVkUbit
import io.verik.compiler.core.declaration.vk.CoreVkUnpacked

object Core {

    object Kt {

        val C_Any = CoreKtClass.C_Any
        val C_Nothing = CoreKtClass.C_Nothing
        val C_Function = CoreKtClass.C_Function
        val C_Unit = CoreKtClass.C_Unit
        val C_Int = CoreKtClass.C_Int
        val C_Boolean = CoreKtClass.C_Boolean
        val C_String = CoreKtClass.C_String
        val C_Enum = CoreKtClass.C_Enum

        val F_repeat_Int_Function = CoreKt.F_repeat_Int_Function
        val F_assert_Boolean = CoreKt.F_assert_Boolean
        val F_assert_Boolean_Function = CoreKt.F_assert_Boolean_Function

        object Int {

            val F_times_Int = CoreKtInt.F_times_Int
            val F_plus_Int = CoreKtInt.F_plus_Int
            val F_minus_Int = CoreKtInt.F_minus_Int
            val F_lt_Int = CoreKtInt.F_lt_Int
            val F_lteq_Int = CoreKtInt.F_lteq_Int
            val F_gt_Int = CoreKtInt.F_gt_Int
            val F_gteq_Int = CoreKtInt.F_gteq_Int
        }

        object Boolean {

            val F_not = CoreKtBoolean.F_not
            val F_and_Boolean = CoreKtBoolean.F_and_Boolean
            val F_or_Boolean = CoreKtBoolean.F_or_Boolean
            val F_xor_Boolean = CoreKtBoolean.F_xor_Boolean
        }

        object Io {

            val F_print_Any = CoreKtIo.F_print_Any
            val F_print_Boolean = CoreKtIo.F_print_Boolean
            val F_print_Int = CoreKtIo.F_print_Int
            val F_println = CoreKtIo.F_println
            val F_println_Any = CoreKtIo.F_println_Any
            val F_println_Boolean = CoreKtIo.F_println_Boolean
            val F_println_Int = CoreKtIo.F_println_Int
        }

        object Collections {

            val F_forEach_Function = CoreKtCollections.F_forEach_Function
        }

        object Ranges {

            val C_IntRange = CoreKtClass.Ranges.C_IntRange

            val F_until_Int = CoreKtRanges.F_until_Int
        }
    }

    object Jv {

        object Util {

            val C_ArrayList = CoreJvClass.Util.C_ArrayList

            val F_ArrayList = CoreJvClass.Util.F_ArrayList

            object ArrayList {

                val F_add_E = CoreJvArrayList.F_add_E

                val F_get_Int = CoreJvArrayList.F_get_Int
                val F_set_Int_E = CoreJvArrayList.F_set_Int_E

                val P_size = CoreJvArrayList.P_size
            }
        }
    }

    object Vk {

        val C_Ubit = CoreVkClass.C_Ubit
        val C_Sbit = CoreVkClass.C_Sbit
        val C_Struct = CoreVkClass.C_Struct
        val C_Packed = CoreVkClass.C_Packed
        val C_Unpacked = CoreVkClass.C_Unpacked
        val C_Component = CoreVkClass.C_Component
        val C_Module = CoreVkClass.C_Module
        val C_ModuleInterface = CoreVkClass.C_ModuleInterface
        val C_ModulePort = CoreVkClass.C_ModulePort
        val C_ClockingBlock = CoreVkClass.C_ClockingBlock
        val C_Time = CoreVkClass.C_Time
        val C_Event = CoreVkClass.C_Event

        val F_Struct = CoreVkClass.F_Struct
        val F_Module = CoreVkClass.F_Module
        val F_ModuleInterface = CoreVkClass.F_ModuleInterface
        val F_ModulePort = CoreVkClass.F_ModulePort
        val F_ClockingBlock = CoreVkClass.F_ClockingBlock

        val N_ADD = CoreVkCardinal.N_ADD
        val N_SUB = CoreVkCardinal.N_SUB
        val N_MUL = CoreVkCardinal.N_MUL
        val N_DIV = CoreVkCardinal.N_DIV
        val N_MAX = CoreVkCardinal.N_MAX
        val N_MIN = CoreVkCardinal.N_MIN
        val N_OF = CoreVkCardinal.N_OF
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
        val F_s_Int = CoreVk.F_s_Int
        val F_s_String = CoreVk.F_s_String
        val F_s_Ubit = CoreVk.F_s_Ubit
        val F_cat_Any = CoreVk.F_cat_Any
        val F_rep_Any = CoreVk.F_rep_Any
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
        val F_fatal_String = CoreVk.F_fatal_String
        val F_error_String = CoreVk.F_error_String
        val F_sv_String = CoreVk.F_sv_String

        val F_Boolean_ext = CoreVk.F_Boolean_ext
        val F_Boolean_sext = CoreVk.F_Boolean_sext

        object Ubit {

            val F_get_Int = CoreVkUbit.F_get_Int
            val F_fill_Int_Boolean = CoreVkUbit.F_fill_Int_Boolean
            val F_fill_Int_Ubit = CoreVkUbit.F_fill_Int_Ubit
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
            val F_shl_Int = CoreVkUbit.F_shl_Int
            val F_shl_Ubit = CoreVkUbit.F_shl_Ubit
            val F_shr_Int = CoreVkUbit.F_shr_Int
            val F_shr_Ubit = CoreVkUbit.F_shr_Ubit
            val F_sshr_Int = CoreVkUbit.F_sshr_Int
            val F_sshr_Ubit = CoreVkUbit.F_sshr_Ubit
            val F_lt_Ubit = CoreVkUbit.F_lt_Ubit
            val F_lteq_Ubit = CoreVkUbit.F_lteq_Ubit
            val F_gt_Ubit = CoreVkUbit.F_gt_Ubit
            val F_gteq_Ubit = CoreVkUbit.F_gteq_Ubit
            val F_invert = CoreVkUbit.F_invert
            val F_reverse = CoreVkUbit.F_reverse
            val F_ext = CoreVkUbit.F_ext
            val F_sext = CoreVkUbit.F_sext
            val F_tru = CoreVkUbit.F_tru
            val F_slice_Int = CoreVkUbit.F_slice_Int
        }

        object Sbit {

            val F_lt_Sbit = CoreVkSbit.F_lt_Sbit
            val F_mul_Sbit = CoreVkSbit.F_mul_Sbit
            val F_plus_Sbit = CoreVkSbit.F_plus_Sbit
        }

        object Unpacked {

            val F_get_Int = CoreVkUnpacked.F_get_Int
            val F_set_Int_E = CoreVkUnpacked.F_set_Int_E
            val F_get_Ubit = CoreVkUnpacked.F_get_Ubit
            val F_set_Ubit_E = CoreVkUnpacked.F_set_Ubit_E
            val F_sort = CoreVkUnpacked.F_sort

            val P_size = CoreVkUnpacked.P_size
        }
    }
}
