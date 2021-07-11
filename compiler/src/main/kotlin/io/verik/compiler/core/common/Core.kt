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

import io.verik.compiler.core.lang.kt.CoreKtInt
import io.verik.compiler.core.lang.kt.CoreKtIo
import io.verik.compiler.core.lang.sv.CoreSv
import io.verik.compiler.core.lang.vk.CoreVk
import io.verik.compiler.core.lang.vk.CoreVkUbit

object Core {

    object Kt {

        val ANY = CoreClassDeclaration(CorePackage.KT.name.name, "Any", null)
        val FUNCTION = CoreClassDeclaration(CorePackage.KT.name.name, "Function", null)
        val UNIT = CoreClassDeclaration(CorePackage.KT.name.name, "Unit", ANY)
        val INT = CoreClassDeclaration(CorePackage.KT.name.name, "Int", ANY)
        val BOOLEAN = CoreClassDeclaration(CorePackage.KT.name.name, "Boolean", ANY)
        val STRING = CoreClassDeclaration(CorePackage.KT.name.name, "String", ANY)

        object Int {
            val TIMES_INT = CoreKtInt.TIMES_INT
            val PLUS_INT = CoreKtInt.PLUS_INT
            val MINUS_INT = CoreKtInt.MINUS_INT
        }

        object Io {

            val PRINT = CoreKtIo.PRINT
            val PRINT_ANY = CoreKtIo.PRINT_ANY
            val PRINT_INT = CoreKtIo.PRINT_INT
            val PRINTLN = CoreKtIo.PRINTLN
            val PRINTLN_ANY = CoreKtIo.PRINTLN_ANY
            val PRINTLN_INT = CoreKtIo.PRINTLN_INT
        }
    }

    object Vk {

        val UBIT = CoreClassDeclaration(CorePackage.VK.name.name, "Ubit", Kt.ANY)
        val SBIT = CoreClassDeclaration(CorePackage.VK.name.name, "Sbit", Kt.ANY)
        val CLASS = CoreClassDeclaration(CorePackage.VK.name.name, "Class", Kt.ANY)
        val EVENT = CoreClassDeclaration(CorePackage.VK.name.name, "Event", Kt.ANY)
        val COMPONENT = CoreClassDeclaration(CorePackage.VK.name.name, "Component", Kt.ANY)
        val MODULE = CoreClassDeclaration(CorePackage.VK.name.name, "Module", COMPONENT)

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

        object Ubit {

            val INV = CoreVkUbit.INV
        }
    }

    object Sv {

        val DISPLAY = CoreSv.DISPLAY
        val WRITE = CoreSv.WRITE
        val SFORMATF = CoreSv.SFORMATF
        val RANDOM = CoreSv.RANDOM
    }
}