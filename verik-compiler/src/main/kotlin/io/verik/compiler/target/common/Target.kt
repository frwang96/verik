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

package io.verik.compiler.target.common

import io.verik.compiler.target.declaration.TargetArrayList
import io.verik.compiler.target.declaration.TargetClasses
import io.verik.compiler.target.declaration.TargetMailbox
import io.verik.compiler.target.declaration.TargetQueue
import io.verik.compiler.target.declaration.TargetSystem

object Target {

    val C_Void = TargetClasses.C_Void
    val C_Boolean = TargetClasses.C_Boolean
    val C_Int = TargetClasses.C_Int
    val C_Double = TargetClasses.C_Double
    val C_String = TargetClasses.C_String
    val C_ArrayList = TargetClasses.C_ArrayList
    val C_Ubit = TargetClasses.C_Ubit
    val C_Sbit = TargetClasses.C_Sbit
    val C_Packed = TargetClasses.C_Packed
    val C_Unpacked = TargetClasses.C_Unpacked
    val C_Queue = TargetClasses.C_Queue
    val C_DynamicArray = TargetClasses.C_DynamicArray
    val C_AssociativeArray = TargetClasses.C_AssociativeArray
    val C_Time = TargetClasses.C_Time
    val C_Event = TargetClasses.C_Event
    val C_Mailbox = TargetClasses.C_Mailbox

    val F_cast = TargetSystem.F_cast
    val F_display = TargetSystem.F_display
    val F_write = TargetSystem.F_write
    val F_sformatf = TargetSystem.F_sformatf
    val F_clog2 = TargetSystem.F_clog2
    val F_random = TargetSystem.F_random
    val F_urandom = TargetSystem.F_urandom
    val F_urandom_range = TargetSystem.F_urandom_range
    val F_unsigned = TargetSystem.F_unsigned
    val F_signed = TargetSystem.F_signed
    val F_time = TargetSystem.F_time
    val F_strobe = TargetSystem.F_strobe
    val F_monitor = TargetSystem.F_monitor
    val F_finish = TargetSystem.F_finish
    val F_fatal = TargetSystem.F_fatal
    val F_error = TargetSystem.F_error
    val F_warning = TargetSystem.F_warning
    val F_info = TargetSystem.F_info
    val F_wait = TargetSystem.F_wait
    val F_name = TargetSystem.F_name
    val F_randomize = TargetSystem.F_randomize
    val F_pre_randomize = TargetSystem.F_pre_randomize
    val F_post_randomize = TargetSystem.F_post_randomize
    val F_sample = TargetSystem.F_sample
    val F_get_coverage = TargetSystem.F_get_coverage

    val P_root = TargetSystem.P_root

    object ArrayList {

        val F_new = TargetArrayList.F_new
        val F_add = TargetArrayList.F_add
        val F_get = TargetArrayList.F_get
        val F_set = TargetArrayList.F_set
        val F_size = TargetArrayList.F_size
    }

    object Queue {

        val F_push_back = TargetQueue.F_push_back
        val F_size = TargetQueue.F_size
    }

    object Mailbox {

        val F_new = TargetMailbox.F_new
        val F_put = TargetMailbox.F_put
        val F_get = TargetMailbox.F_get
    }
}
