/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.kt

import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreClassDeclaration
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.target.common.Target

/**
 * Core classes from the Kotlin standard library.
 */
object CoreKtClasses : CoreScope(CorePackage.KT) {

    val C_Function = CoreClassDeclaration(parent, "Function", null, Target.C_Void)
    val C_Any = CoreClassDeclaration(parent, "Any", null, Target.C_Void)
    val C_Unit = CoreClassDeclaration(parent, "Unit", C_Any, Target.C_Void)
    val C_Nothing = CoreClassDeclaration(parent, "Nothing", null, Target.C_Void)
    val C_Enum = CoreClassDeclaration(parent, "Enum", C_Any, null)
    val C_Boolean = CoreClassDeclaration(parent, "Boolean", C_Any, Target.C_Boolean)
    val C_Int = CoreClassDeclaration(parent, "Int", C_Any, Target.C_Int)
    val C_Double = CoreClassDeclaration(parent, "Double", C_Any, Target.C_Double)
    val C_String = CoreClassDeclaration(parent, "String", C_Any, Target.C_String)

    object Ranges : CoreScope(CorePackage.KT_RANGES) {

        val C_IntRange = CoreClassDeclaration(parent, "IntRange", Core.Kt.C_Any, null)
    }
}
