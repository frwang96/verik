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
import io.verik.compiler.core.declaration.kt.CoreKtBoolean
import io.verik.compiler.core.declaration.kt.CoreKtClass
import io.verik.compiler.core.declaration.kt.CoreKtCollections
import io.verik.compiler.core.declaration.kt.CoreKtFunctions
import io.verik.compiler.core.declaration.kt.CoreKtInt
import io.verik.compiler.core.declaration.kt.CoreKtIo
import io.verik.compiler.core.declaration.kt.CoreKtRanges
import io.verik.compiler.core.declaration.kt.CoreKtText
import io.verik.compiler.core.declaration.vk.CoreVkBoolean
import io.verik.compiler.core.declaration.vk.CoreVkCardinal
import io.verik.compiler.core.declaration.vk.CoreVkClass
import io.verik.compiler.core.declaration.vk.CoreVkControl
import io.verik.compiler.core.declaration.vk.CoreVkMisc
import io.verik.compiler.core.declaration.vk.CoreVkPacked
import io.verik.compiler.core.declaration.vk.CoreVkRandom
import io.verik.compiler.core.declaration.vk.CoreVkSbit
import io.verik.compiler.core.declaration.vk.CoreVkSbitBinary
import io.verik.compiler.core.declaration.vk.CoreVkSpecial
import io.verik.compiler.core.declaration.vk.CoreVkSystem
import io.verik.compiler.core.declaration.vk.CoreVkUbit
import io.verik.compiler.core.declaration.vk.CoreVkUbitBinary
import io.verik.compiler.core.declaration.vk.CoreVkUnpacked

object Core {

    object Kt {

        val C_Function = CoreKtClass.C_Function
        val C_Any = CoreKtClass.C_Any
        val C_Unit = CoreKtClass.C_Unit
        val C_Nothing = CoreKtClass.C_Nothing
        val C_Enum = CoreKtClass.C_Enum
        val C_Int = CoreKtClass.C_Int
        val C_Boolean = CoreKtClass.C_Boolean
        val C_String = CoreKtClass.C_String

        val F_repeat_Int_Function = CoreKtFunctions.F_repeat_Int_Function
        val F_assert_Boolean = CoreKtFunctions.F_assert_Boolean
        val F_assert_Boolean_Function = CoreKtFunctions.F_assert_Boolean_Function

        object Int {

            val F_plus_Int = CoreKtInt.F_plus_Int
            val F_minus_Int = CoreKtInt.F_minus_Int
            val F_times_Int = CoreKtInt.F_times_Int
            val F_shl_Int = CoreKtInt.F_shl_Int
            val F_shr_Int = CoreKtInt.F_shr_Int
            val F_ushr_Int = CoreKtInt.F_ushr_Int
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

        object Text {

            val F_trimIndent = CoreKtText.F_trimIndent
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
        val C_Packed = CoreVkClass.C_Packed
        val C_Unpacked = CoreVkClass.C_Unpacked
        val C_Time = CoreVkClass.C_Time
        val C_Event = CoreVkClass.C_Event
        val C_Struct = CoreVkClass.C_Struct
        val C_Component = CoreVkClass.C_Component
        val C_Module = CoreVkClass.C_Module
        val C_ModuleInterface = CoreVkClass.C_ModuleInterface
        val C_ModulePort = CoreVkClass.C_ModulePort
        val C_ClockingBlock = CoreVkClass.C_ClockingBlock

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

        val F_finish = CoreVkSystem.F_finish
        val F_fatal = CoreVkSystem.F_fatal
        val F_fatal_String = CoreVkSystem.F_fatal_String
        val F_error_String = CoreVkSystem.F_error_String
        val F_time = CoreVkSystem.F_time

        val F_imported = CoreVkSpecial.F_imported
        val F_sv_String = CoreVkSpecial.F_sv_String
        val F_nc = CoreVkSpecial.F_nc
        val F_i = CoreVkSpecial.F_i
        val F_u = CoreVkSpecial.F_u
        val F_u_Boolean = CoreVkSpecial.F_u_Boolean
        val F_u_Int = CoreVkSpecial.F_u_Int
        val F_u_String = CoreVkSpecial.F_u_String
        val F_u_Sbit = CoreVkSpecial.F_u_Sbit
        val F_u0 = CoreVkSpecial.F_u0
        val F_s_Int = CoreVkSpecial.F_s_Int
        val F_s_String = CoreVkSpecial.F_s_String
        val F_s_Ubit = CoreVkSpecial.F_s_Ubit

        val F_posedge_Boolean = CoreVkControl.F_posedge_Boolean
        val F_negedge_Boolean = CoreVkControl.F_negedge_Boolean
        val F_on_Event_Event_Function = CoreVkControl.F_on_Event_Event_Function
        val F_forever_Function = CoreVkControl.F_forever_Function
        val F_delay_Int = CoreVkControl.F_delay_Int
        val F_wait_Boolean = CoreVkControl.F_wait_Boolean
        val F_wait_Event = CoreVkControl.F_wait_Event
        val F_wait_ClockingBlock = CoreVkControl.F_wait_ClockingBlock

        val F_cat_Any_Any = CoreVkMisc.F_cat_Any_Any
        val F_rep_Any = CoreVkMisc.F_rep_Any
        val F_max_Int_Int = CoreVkMisc.F_max_Int_Int
        val F_max_Ubit_Ubit = CoreVkMisc.F_max_Ubit_Ubit
        val F_max_Sbit_Sbit = CoreVkMisc.F_max_Sbit_Sbit
        val F_min_Int_Int = CoreVkMisc.F_min_Int_Int
        val F_min_Ubit_Ubit = CoreVkMisc.F_min_Ubit_Ubit
        val F_min_Sbit_Sbit = CoreVkMisc.F_min_Sbit_Sbit
        val F_log_Int = CoreVkMisc.F_log_Int
        val F_exp_Int = CoreVkMisc.F_exp_Int

        val F_random = CoreVkRandom.F_random
        val F_random_Int = CoreVkRandom.F_random_Int
        val F_random_Int_Int = CoreVkRandom.F_random_Int_Int
        val F_randomBoolean = CoreVkRandom.F_randomBoolean
        val F_randomUbit = CoreVkRandom.F_randomUbit

        object Boolean {

            val F_Boolean_ext = CoreVkBoolean.F_ext
            val F_Boolean_sext = CoreVkBoolean.F_sext
        }

        object Ubit {

            val F_unaryPlus = CoreVkUbit.F_unaryPlus
            val F_unaryMinus = CoreVkUbit.F_unaryMinus
            val F_get_Int = CoreVkUbit.F_get_Int
            val F_get_Ubit = CoreVkUbit.F_get_Ubit
            val F_set_Int_Boolean = CoreVkUbit.F_set_Int_Boolean
            val F_set_Ubit_Boolean = CoreVkUbit.F_set_Ubit_Boolean
            val F_set_Int_Ubit = CoreVkUbit.F_set_Int_Ubit
            val F_set_Ubit_Ubit = CoreVkUbit.F_set_Ubit_Ubit
            val F_not = CoreVkUbit.F_not

            val F_plus_Ubit = CoreVkUbitBinary.F_plus_Ubit
            val F_plus_Sbit = CoreVkUbitBinary.F_plus_Sbit
            val F_add_Ubit = CoreVkUbitBinary.F_add_Ubit
            val F_add_Sbit = CoreVkUbitBinary.F_add_Sbit
            val F_minus_Ubit = CoreVkUbitBinary.F_minus_Ubit
            val F_minus_Sbit = CoreVkUbitBinary.F_minus_Sbit
            val F_times_Ubit = CoreVkUbitBinary.F_times_Ubit
            val F_times_Sbit = CoreVkUbitBinary.F_times_Sbit
            val F_mul_Ubit = CoreVkUbitBinary.F_mul_Ubit
            val F_mul_Sbit = CoreVkUbitBinary.F_mul_Sbit
            val F_div_Ubit = CoreVkUbitBinary.F_div_Ubit
            val F_and_Ubit = CoreVkUbitBinary.F_and_Ubit
            val F_and_Sbit = CoreVkUbitBinary.F_and_Sbit
            val F_or_Ubit = CoreVkUbitBinary.F_or_Ubit
            val F_or_Sbit = CoreVkUbitBinary.F_or_Sbit
            val F_xor_Ubit = CoreVkUbitBinary.F_xor_Ubit
            val F_xor_Sbit = CoreVkUbitBinary.F_xor_Sbit
            val F_shl_Int = CoreVkUbitBinary.F_shl_Int
            val F_shl_Ubit = CoreVkUbitBinary.F_shl_Ubit
            val F_shr_Int = CoreVkUbitBinary.F_shr_Int
            val F_shr_Ubit = CoreVkUbitBinary.F_shr_Ubit
            val F_sshr_Int = CoreVkUbitBinary.F_sshr_Int
            val F_sshr_Ubit = CoreVkUbitBinary.F_sshr_Ubit

            val F_invert = CoreVkUbit.F_invert
            val F_reverse = CoreVkUbit.F_reverse
            val F_reduceAnd = CoreVkUbit.F_reduceAnd
            val F_reduceOr = CoreVkUbit.F_reduceOr
            val F_reduceXor = CoreVkUbit.F_reduceXor
            val F_slice_Int = CoreVkUbit.F_slice_Int
            val F_slice_Ubit = CoreVkUbit.F_slice_Ubit
            val F_ext = CoreVkUbit.F_ext
            val F_sext = CoreVkUbit.F_sext
            val F_tru = CoreVkUbit.F_tru
        }

        object Sbit {

            val F_unaryPlus = CoreVkSbit.F_unaryPlus
            val F_unaryMinus = CoreVkSbit.F_unaryMinus

            val F_plus_Sbit = CoreVkSbitBinary.F_plus_Sbit
            val F_mul_Sbit = CoreVkSbitBinary.F_mul_Sbit
        }

        object Packed {

            val F_get_Int = CoreVkPacked.F_get_Int
            val F_get_Ubit = CoreVkPacked.F_get_Ubit
            val F_set_Int_E = CoreVkPacked.F_set_Int_E
            val F_set_Ubit_E = CoreVkPacked.F_set_Ubit_E

            val P_size = CoreVkPacked.P_size
        }

        object Unpacked {

            val F_get_Int = CoreVkUnpacked.F_get_Int
            val F_get_Ubit = CoreVkUnpacked.F_get_Ubit
            val F_set_Int_E = CoreVkUnpacked.F_set_Int_E
            val F_set_Ubit_E = CoreVkUnpacked.F_set_Ubit_E

            val P_size = CoreVkUnpacked.P_size
        }
    }
}
