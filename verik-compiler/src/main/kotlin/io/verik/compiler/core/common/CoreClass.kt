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

import io.verik.compiler.target.common.Target

object CoreClass {

    object Kt : CoreScope(CorePackage.KT) {

        val C_Any = CoreClassDeclaration(parent, "Any", null, null)
        val C_Nothing = CoreClassDeclaration(parent, "Nothing", null, Target.C_Void)
        val C_Function = CoreClassDeclaration(parent, "Function", null, Target.C_Void)
        val C_Unit = CoreClassDeclaration(parent, "Unit", C_Any, Target.C_Void)
        val C_Int = CoreClassDeclaration(parent, "Int", C_Any, Target.C_Int)
        val C_Boolean = CoreClassDeclaration(parent, "Boolean", C_Any, Target.C_Boolean)
        val C_String = CoreClassDeclaration(parent, "String", C_Any, Target.C_String)
        val C_Enum = CoreClassDeclaration(parent, "Enum", C_Any, null)

        object Ranges : CoreScope(CorePackage.Kt_RANGES) {

            val C_IntRange = CoreClassDeclaration(parent, "IntRange", C_Any, null)
        }
    }

    object Jv : CoreScope(CorePackage.JV) {

        object Util : CoreScope(CorePackage.JV_UTIL) {

            val C_ArrayList = CoreClassDeclaration(parent, "ArrayList", Kt.C_Any, Target.C_ArrayList)
        }
    }

    object Vk : CoreScope(CorePackage.VK) {

        val C_Ubit = CoreClassDeclaration(parent, "Ubit", Kt.C_Any, Target.C_Ubit)
        val C_Sbit = CoreClassDeclaration(parent, "Sbit", Kt.C_Any, Target.C_Sbit)
        val C_Struct = CoreClassDeclaration(parent, "Struct", Kt.C_Any, null)
        val C_Packed = CoreClassDeclaration(parent, "Packed", Kt.C_Any, Target.C_Packed)
        val C_Unpacked = CoreClassDeclaration(parent, "Unpacked", Kt.C_Any, Target.C_Unpacked)
        val C_Component = CoreClassDeclaration(parent, "Component", Kt.C_Any, null)
        val C_Module = CoreClassDeclaration(parent, "Module", C_Component, null)
        val C_ModuleInterface = CoreClassDeclaration(parent, "ModuleInterface", C_Component, null)
        val C_ModulePort = CoreClassDeclaration(parent, "ModulePort", C_Component, null)
        val C_ClockingBlock = CoreClassDeclaration(parent, "ClockingBlock", C_Component, null)
        val C_Time = CoreClassDeclaration(parent, "Time", Kt.C_Any, null)
        val C_Event = CoreClassDeclaration(parent, "Event", Kt.C_Any, null)
    }
}
