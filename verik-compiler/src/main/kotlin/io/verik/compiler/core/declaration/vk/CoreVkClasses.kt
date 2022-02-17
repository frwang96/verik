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

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreClassDeclaration
import io.verik.compiler.core.common.CoreConstructorDeclaration
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.target.common.Target

object CoreVkClasses : CoreScope(CorePackage.VK) {

    val C_Ubit = CoreClassDeclaration(parent, "Ubit", Core.Kt.C_Any, Target.C_Ubit)
    val C_Sbit = CoreClassDeclaration(parent, "Sbit", Core.Kt.C_Any, Target.C_Sbit)
    val C_Packed = CoreClassDeclaration(parent, "Packed", Core.Kt.C_Any, Target.C_Packed)
    val C_Unpacked = CoreClassDeclaration(parent, "Unpacked", Core.Kt.C_Any, Target.C_Unpacked)
    val C_Queue = CoreClassDeclaration(parent, "Queue", Core.Kt.C_Any, Target.C_Queue)
    val C_DynamicArray = CoreClassDeclaration(parent, "DynamicArray", Core.Kt.C_Any, Target.C_DynamicArray)
    val C_AssociativeArray = CoreClassDeclaration(parent, "AssociativeArray", Core.Kt.C_Any, Target.C_AssociativeArray)
    val C_Time = CoreClassDeclaration(parent, "Time", Core.Kt.C_Any, Target.C_Time)
    val C_Event = CoreClassDeclaration(parent, "Event", Core.Kt.C_Any, Target.C_Event)
    val C_Constraint = CoreClassDeclaration(parent, "Constraint", Core.Kt.C_Any, Target.C_Void)
    val C_Class = CoreClassDeclaration(parent, "Class", Core.Kt.C_Any, null)
    val C_Struct = CoreClassDeclaration(parent, "Struct", Core.Kt.C_Any, null)
    val C_Component = CoreClassDeclaration(parent, "Component", Core.Kt.C_Any, null)
    val C_Module = CoreClassDeclaration(parent, "Module", C_Component, null)
    val C_ModuleInterface = CoreClassDeclaration(parent, "ModuleInterface", C_Component, null)
    val C_ModulePort = CoreClassDeclaration(parent, "ModulePort", C_Component, null)
    val C_ClockingBlock = CoreClassDeclaration(parent, "ClockingBlock", C_Component, null)

    val F_Class = CoreConstructorDeclaration(C_Class, null)
    val F_Struct = CoreConstructorDeclaration(C_Struct, null)
    val F_Module = CoreConstructorDeclaration(C_Module, null)
    val F_ModuleInterface = CoreConstructorDeclaration(C_ModuleInterface, null)
    val F_ModulePort = CoreConstructorDeclaration(C_ModulePort, null)
    val F_ClockingBlock = CoreConstructorDeclaration(C_ClockingBlock, null)
}
