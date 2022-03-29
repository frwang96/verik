/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package io.verik.compiler.core.common

import io.verik.compiler.core.declaration.jv.CoreJvArrayList
import io.verik.compiler.core.declaration.jv.CoreJvClasses
import io.verik.compiler.core.declaration.kt.CoreKtAny
import io.verik.compiler.core.declaration.kt.CoreKtBoolean
import io.verik.compiler.core.declaration.kt.CoreKtClasses
import io.verik.compiler.core.declaration.kt.CoreKtCollections
import io.verik.compiler.core.declaration.kt.CoreKtDouble
import io.verik.compiler.core.declaration.kt.CoreKtFunctions
import io.verik.compiler.core.declaration.kt.CoreKtInt
import io.verik.compiler.core.declaration.kt.CoreKtIo
import io.verik.compiler.core.declaration.kt.CoreKtRanges
import io.verik.compiler.core.declaration.kt.CoreKtString
import io.verik.compiler.core.declaration.kt.CoreKtText
import io.verik.compiler.core.declaration.vk.CoreVkAssociativeArray
import io.verik.compiler.core.declaration.vk.CoreVkBoolean
import io.verik.compiler.core.declaration.vk.CoreVkCardinal
import io.verik.compiler.core.declaration.vk.CoreVkClass
import io.verik.compiler.core.declaration.vk.CoreVkClasses
import io.verik.compiler.core.declaration.vk.CoreVkCluster
import io.verik.compiler.core.declaration.vk.CoreVkControl
import io.verik.compiler.core.declaration.vk.CoreVkCoverGroup
import io.verik.compiler.core.declaration.vk.CoreVkData
import io.verik.compiler.core.declaration.vk.CoreVkEvent
import io.verik.compiler.core.declaration.vk.CoreVkMailbox
import io.verik.compiler.core.declaration.vk.CoreVkMisc
import io.verik.compiler.core.declaration.vk.CoreVkPacked
import io.verik.compiler.core.declaration.vk.CoreVkQueue
import io.verik.compiler.core.declaration.vk.CoreVkRandom
import io.verik.compiler.core.declaration.vk.CoreVkSbit
import io.verik.compiler.core.declaration.vk.CoreVkSbitBinary
import io.verik.compiler.core.declaration.vk.CoreVkSpecial
import io.verik.compiler.core.declaration.vk.CoreVkSystem
import io.verik.compiler.core.declaration.vk.CoreVkUbit
import io.verik.compiler.core.declaration.vk.CoreVkUbitBinary
import io.verik.compiler.core.declaration.vk.CoreVkUnpacked

/**
 * List of all the core declarations supported by the compiler.
 */
object Core {

    object Kt {

        val C_Function = CoreKtClasses.C_Function
        val C_Any = CoreKtClasses.C_Any
        val C_Unit = CoreKtClasses.C_Unit
        val C_Nothing = CoreKtClasses.C_Nothing
        val C_Enum = CoreKtClasses.C_Enum
        val C_Boolean = CoreKtClasses.C_Boolean
        val C_Int = CoreKtClasses.C_Int
        val C_Double = CoreKtClasses.C_Double
        val C_String = CoreKtClasses.C_String

        val F_repeat_Int_Function = CoreKtFunctions.F_repeat_Int_Function
        val F_assert_Boolean = CoreKtFunctions.F_assert_Boolean
        val F_assert_Boolean_Function = CoreKtFunctions.F_assert_Boolean_Function

        object Any {

            val F_toString = CoreKtAny.F_toString
        }

        object Boolean {

            val F_not = CoreKtBoolean.F_not
            val F_and_Boolean = CoreKtBoolean.F_and_Boolean
            val F_or_Boolean = CoreKtBoolean.F_or_Boolean
            val F_xor_Boolean = CoreKtBoolean.F_xor_Boolean
        }

        object Int {

            val F_unaryPlus = CoreKtInt.F_unaryPlus
            val F_unaryMinus = CoreKtInt.F_unaryMinus
            val F_rangeTo_Int = CoreKtInt.F_rangeTo_Int
            val F_plus_Int = CoreKtInt.F_plus_Int
            val F_minus_Int = CoreKtInt.F_minus_Int
            val F_times_Int = CoreKtInt.F_times_Int
            val F_shl_Int = CoreKtInt.F_shl_Int
            val F_shr_Int = CoreKtInt.F_shr_Int
            val F_ushr_Int = CoreKtInt.F_ushr_Int
        }

        object Double {

            val F_plus_Int = CoreKtDouble.F_plus_Int
            val F_plus_Double = CoreKtDouble.F_plus_Double
            val F_div_Int = CoreKtDouble.F_div_Int
        }

        object String {

            val F_plus_Any = CoreKtString.F_plus_Any
        }

        object Io {

            val F_print_Any = CoreKtIo.F_print_Any
            val F_print_Boolean = CoreKtIo.F_print_Boolean
            val F_print_Int = CoreKtIo.F_print_Int
            val F_print_Double = CoreKtIo.F_print_Double
            val F_println = CoreKtIo.F_println
            val F_println_Any = CoreKtIo.F_println_Any
            val F_println_Boolean = CoreKtIo.F_println_Boolean
            val F_println_Int = CoreKtIo.F_println_Int
            val F_println_Double = CoreKtIo.F_println_Double
        }

        object Collections {

            val F_forEach_Function = CoreKtCollections.F_forEach_Function
        }

        object Text {

            val F_trimIndent = CoreKtText.F_trimIndent
        }

        object Ranges {

            val C_IntRange = CoreKtClasses.Ranges.C_IntRange

            val F_until_Int = CoreKtRanges.F_until_Int
        }
    }

    object Jv {

        object Util {

            val C_ArrayList = CoreJvClasses.Util.C_ArrayList

            val F_ArrayList = CoreJvClasses.Util.F_ArrayList

            object ArrayList {

                val P_size = CoreJvArrayList.P_size
                val F_add_E = CoreJvArrayList.F_add_E
                val F_get_Int = CoreJvArrayList.F_get_Int
                val F_set_Int_E = CoreJvArrayList.F_set_Int_E
            }
        }
    }

    object Vk {

        val C_Ubit = CoreVkClasses.C_Ubit
        val C_Sbit = CoreVkClasses.C_Sbit
        val C_Packed = CoreVkClasses.C_Packed
        val C_Unpacked = CoreVkClasses.C_Unpacked
        val C_Queue = CoreVkClasses.C_Queue
        val C_DynamicArray = CoreVkClasses.C_DynamicArray
        val C_AssociativeArray = CoreVkClasses.C_AssociativeArray
        val C_Time = CoreVkClasses.C_Time
        val C_Event = CoreVkClasses.C_Event
        val C_Mailbox = CoreVkClasses.C_Mailbox
        val C_Constraint = CoreVkClasses.C_Constraint
        val C_Struct = CoreVkClasses.C_Struct
        val C_Union = CoreVkClasses.C_Union
        val C_Class = CoreVkClasses.C_Class
        val C_CoverGroup = CoreVkClasses.C_CoverGroup
        val C_CoverPoint = CoreVkClasses.C_CoverPoint
        val C_CoverCross = CoreVkClasses.C_CoverCross
        val C_Component = CoreVkClasses.C_Component
        val C_Module = CoreVkClasses.C_Module
        val C_ModuleInterface = CoreVkClasses.C_ModuleInterface
        val C_ModulePort = CoreVkClasses.C_ModulePort
        val C_ClockingBlock = CoreVkClasses.C_ClockingBlock
        val C_Cluster = CoreVkClasses.C_Cluster

        val F_Mailbox = CoreVkClasses.F_Mailbox
        val F_Struct = CoreVkClasses.F_Struct
        val F_Union = CoreVkClasses.F_Union
        val F_Class = CoreVkClasses.F_Class
        val F_CoverGroup = CoreVkClasses.F_CoverGroup
        val F_Module = CoreVkClasses.F_Module
        val F_ModuleInterface = CoreVkClasses.F_ModuleInterface
        val F_ModulePort = CoreVkClasses.F_ModulePort
        val F_ClockingBlock = CoreVkClasses.F_ClockingBlock

        val T_TRUE = CoreVkCardinal.T_TRUE
        val T_FALSE = CoreVkCardinal.T_FALSE
        val T_NOT = CoreVkCardinal.T_NOT
        val T_AND = CoreVkCardinal.T_AND
        val T_OR = CoreVkCardinal.T_OR
        val T_IF = CoreVkCardinal.T_IF
        val T_ADD = CoreVkCardinal.T_ADD
        val T_SUB = CoreVkCardinal.T_SUB
        val T_MUL = CoreVkCardinal.T_MUL
        val T_DIV = CoreVkCardinal.T_DIV
        val T_MAX = CoreVkCardinal.T_MAX
        val T_MIN = CoreVkCardinal.T_MIN
        val T_OF = CoreVkCardinal.T_OF
        val T_INC = CoreVkCardinal.T_INC
        val T_DEC = CoreVkCardinal.T_DEC
        val T_LOG = CoreVkCardinal.T_LOG
        val T_WIDTH = CoreVkCardinal.T_WIDTH
        val T_EXP = CoreVkCardinal.T_EXP

        val F_strobe_String = CoreVkSystem.F_strobe_String
        val F_monitor_String = CoreVkSystem.F_monitor_String
        val F_finish = CoreVkSystem.F_finish
        val F_fatal = CoreVkSystem.F_fatal
        val F_fatal_String = CoreVkSystem.F_fatal_String
        val F_error_String = CoreVkSystem.F_error_String
        val F_warning_String = CoreVkSystem.F_warning_String
        val F_info_String = CoreVkSystem.F_info_String
        val F_time = CoreVkSystem.F_time

        val F_imp = CoreVkSpecial.F_imp
        val F_inj_String = CoreVkSpecial.F_inj_String
        val F_inji_String = CoreVkSpecial.F_inji_String
        val F_t = CoreVkSpecial.F_t
        val F_nc = CoreVkSpecial.F_nc
        val F_c_Boolean = CoreVkSpecial.F_c_Boolean
        val F_cp_Any_String = CoreVkSpecial.F_cp_Any_String
        val F_cc_CoverPoint_CoverPoint_String = CoreVkSpecial.F_cc_CoverPoint_CoverPoint_String
        val F_cc_CoverPoint_CoverPoint_CoverPoint_String = CoreVkSpecial.F_cc_CoverPoint_CoverPoint_CoverPoint_String
        val F_optional_Boolean_Function = CoreVkSpecial.F_optional_Boolean_Function
        val F_cluster_Int_Function = CoreVkSpecial.F_cluster_Int_Function

        val P_unknown = CoreVkData.P_unknown
        val P_floating = CoreVkData.P_floating
        val F_b = CoreVkData.F_b
        val F_i = CoreVkData.F_i
        val F_u = CoreVkData.F_u
        val F_u_Int = CoreVkData.F_u_Int
        val F_u_String = CoreVkData.F_u_String
        val F_u0 = CoreVkData.F_u0
        val F_u1 = CoreVkData.F_u1
        val F_ux = CoreVkData.F_ux
        val F_uz = CoreVkData.F_uz
        val F_s_Int = CoreVkData.F_s_Int
        val F_s_String = CoreVkData.F_s_String
        val F_s0 = CoreVkData.F_s0
        val F_s1 = CoreVkData.F_s1
        val F_fill0 = CoreVkData.F_fill0
        val F_fill1 = CoreVkData.F_fill1
        val F_fillx = CoreVkData.F_fillx
        val F_fillz = CoreVkData.F_fillz

        val F_posedge_Boolean = CoreVkControl.F_posedge_Boolean
        val F_negedge_Boolean = CoreVkControl.F_negedge_Boolean
        val F_on_Event_Function = CoreVkControl.F_on_Event_Function
        val F_oni_Event_Function = CoreVkControl.F_oni_Event_Function
        val F_forever_Function = CoreVkControl.F_forever_Function
        val F_delay_Int = CoreVkControl.F_delay_Int
        val F_wait_Boolean = CoreVkControl.F_wait_Boolean
        val F_wait_Event = CoreVkControl.F_wait_Event
        val F_wait_ClockingBlock = CoreVkControl.F_wait_ClockingBlock
        val F_fork_Function = CoreVkControl.F_fork_Function
        val F_join = CoreVkControl.F_join

        val F_cat_Any = CoreVkMisc.F_cat_Any
        val F_rep_Any = CoreVkMisc.F_rep_Any
        val F_max_Int = CoreVkMisc.F_max_Int
        val F_max_Ubit = CoreVkMisc.F_max_Ubit
        val F_max_Sbit = CoreVkMisc.F_max_Sbit
        val F_min_Int = CoreVkMisc.F_min_Int
        val F_min_Ubit = CoreVkMisc.F_min_Ubit
        val F_min_Sbit = CoreVkMisc.F_min_Sbit
        val F_log_Int = CoreVkMisc.F_log_Int
        val F_exp_Int = CoreVkMisc.F_exp_Int

        val F_random = CoreVkRandom.F_random
        val F_random_Int = CoreVkRandom.F_random_Int
        val F_random_Int_Int = CoreVkRandom.F_random_Int_Int
        val F_randomBoolean = CoreVkRandom.F_randomBoolean
        val F_randomUbit = CoreVkRandom.F_randomUbit

        object Boolean {

            val F_Boolean_toUbit = CoreVkBoolean.F_toUbit
            val F_Boolean_toSbit = CoreVkBoolean.F_toSbit
        }

        object Ubit {

            val F_unaryPlus = CoreVkUbit.F_unaryPlus
            val F_unaryMinus = CoreVkUbit.F_unaryMinus
            val F_get_Int = CoreVkUbit.F_get_Int
            val F_get_Ubit = CoreVkUbit.F_get_Ubit
            val F_get_Int_Int = CoreVkUbit.F_get_Int_Int
            val F_set_Int_Boolean = CoreVkUbit.F_set_Int_Boolean
            val F_set_Ubit_Boolean = CoreVkUbit.F_set_Ubit_Boolean
            val F_set_Int_Int_Ubit = CoreVkUbit.F_set_Int_Int_Ubit
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

            val F_inv = CoreVkUbit.F_inv
            val F_rev = CoreVkUbit.F_rev
            val F_andRed = CoreVkUbit.F_andRed
            val F_orRed = CoreVkUbit.F_orRed
            val F_xorRed = CoreVkUbit.F_xorRed
            val F_eqz = CoreVkUbit.F_eqz
            val F_neqz = CoreVkUbit.F_neqz
            val F_ext = CoreVkUbit.F_ext
            val F_sext = CoreVkUbit.F_sext
            val F_tru = CoreVkUbit.F_tru
            val F_res = CoreVkUbit.F_res
            val F_sres = CoreVkUbit.F_sres
            val F_toSbit = CoreVkUbit.F_toSbit
            val F_toBinString = CoreVkUbit.F_toBinString
            val F_toDecString = CoreVkUbit.F_toDecString
            val F_toHexString = CoreVkUbit.F_toHexString
        }

        object Sbit {

            val F_unaryPlus = CoreVkSbit.F_unaryPlus
            val F_unaryMinus = CoreVkSbit.F_unaryMinus
            val F_get_Int = CoreVkSbit.F_get_Int
            val F_get_Ubit = CoreVkSbit.F_get_Ubit
            val F_set_Int_Boolean = CoreVkSbit.F_set_Int_Boolean
            val F_set_Ubit_Boolean = CoreVkSbit.F_set_Ubit_Boolean
            val F_not = CoreVkSbit.F_not

            val F_plus_Ubit = CoreVkSbitBinary.F_plus_Ubit
            val F_plus_Sbit = CoreVkSbitBinary.F_plus_Sbit
            val F_add_Ubit = CoreVkSbitBinary.F_add_Ubit
            val F_add_Sbit = CoreVkSbitBinary.F_add_Sbit
            val F_minus_Ubit = CoreVkSbitBinary.F_minus_Ubit
            val F_minus_Sbit = CoreVkSbitBinary.F_minus_Sbit
            val F_times_Ubit = CoreVkSbitBinary.F_times_Ubit
            val F_times_Sbit = CoreVkSbitBinary.F_times_Sbit
            val F_mul_Ubit = CoreVkSbitBinary.F_mul_Ubit
            val F_mul_Sbit = CoreVkSbitBinary.F_mul_Sbit
            val F_and_Ubit = CoreVkSbitBinary.F_and_Ubit
            val F_and_Sbit = CoreVkSbitBinary.F_and_Sbit
            val F_or_Ubit = CoreVkSbitBinary.F_or_Ubit
            val F_or_Sbit = CoreVkSbitBinary.F_or_Sbit
            val F_xor_Ubit = CoreVkSbitBinary.F_xor_Ubit
            val F_xor_Sbit = CoreVkSbitBinary.F_xor_Sbit
            val F_shl_Int = CoreVkSbitBinary.F_shl_Int
            val F_shl_Ubit = CoreVkSbitBinary.F_shl_Ubit
            val F_shr_Int = CoreVkSbitBinary.F_shr_Int
            val F_shr_Ubit = CoreVkSbitBinary.F_shr_Ubit
            val F_ushr_Int = CoreVkSbitBinary.F_ushr_Int
            val F_ushr_Ubit = CoreVkSbitBinary.F_ushr_Ubit

            val F_ext = CoreVkSbit.F_ext
            val F_uext = CoreVkSbit.F_uext
            val F_tru = CoreVkSbit.F_tru
            val F_toUbit = CoreVkSbit.F_toUbit
        }

        object Packed {

            val P_size = CoreVkPacked.P_size
            val F_get_Int = CoreVkPacked.F_get_Int
            val F_get_Ubit = CoreVkPacked.F_get_Ubit
            val F_set_Int_E = CoreVkPacked.F_set_Int_E
            val F_set_Ubit_E = CoreVkPacked.F_set_Ubit_E
        }

        object Unpacked {

            val P_size = CoreVkUnpacked.P_size
            val F_get_Int = CoreVkUnpacked.F_get_Int
            val F_get_Ubit = CoreVkUnpacked.F_get_Ubit
            val F_set_Int_E = CoreVkUnpacked.F_set_Int_E
            val F_set_Ubit_E = CoreVkUnpacked.F_set_Ubit_E
        }

        object Queue {

            val P_size = CoreVkQueue.P_size
            val F_add_E = CoreVkQueue.F_add_E
            val F_get_Int = CoreVkQueue.F_get_Int
        }

        object AssociativeArray {

            val F_set_K_V = CoreVkAssociativeArray.F_set_K_V
            val F_get_K = CoreVkAssociativeArray.F_get_K
        }

        object Event {

            val F_trigger = CoreVkEvent.F_trigger
        }

        object Mailbox {

            val F_put_T = CoreVkMailbox.F_put_T
            val F_get = CoreVkMailbox.F_get
        }

        object Class {

            val F_randomize = CoreVkClass.F_randomize
            val F_preRandomize = CoreVkClass.F_preRandomize
            val F_postRandomize = CoreVkClass.F_postRandomize
        }

        object CoverGroup {

            val F_sample = CoreVkCoverGroup.F_sample
            val F_coverage = CoreVkCoverGroup.F_coverage
        }

        object Cluster {

            val F_get_Int = CoreVkCluster.F_get_Int
            val F_map_Function = CoreVkCluster.F_map_Function
        }
    }
}
