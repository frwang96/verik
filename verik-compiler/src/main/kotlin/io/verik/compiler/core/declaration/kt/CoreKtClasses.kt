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

package io.verik.compiler.core.declaration.kt

import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreClassDeclaration
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.target.common.Target

object CoreKtClasses : CoreScope(CorePackage.KT) {

    val C_Function = CoreClassDeclaration(parent, "Function", null, Target.C_Void)
    val C_Any = CoreClassDeclaration(parent, "Any", null, null)
    val C_Unit = CoreClassDeclaration(parent, "Unit", C_Any, Target.C_Void)
    val C_Nothing = CoreClassDeclaration(parent, "Nothing", null, Target.C_Void)
    val C_Enum = CoreClassDeclaration(parent, "Enum", C_Any, null)
    val C_Int = CoreClassDeclaration(parent, "Int", C_Any, Target.C_Int)
    val C_Boolean = CoreClassDeclaration(parent, "Boolean", C_Any, Target.C_Boolean)
    val C_String = CoreClassDeclaration(parent, "String", C_Any, Target.C_String)

    object Ranges : CoreScope(CorePackage.KT_RANGES) {

        val C_IntRange = CoreClassDeclaration(parent, "IntRange", Core.Kt.C_Any, null)
    }
}
