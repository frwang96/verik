/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.jv

import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreClassDeclaration
import io.verik.compiler.core.common.CoreConstructorDeclaration
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.target.common.Target

object CoreJvClasses : CoreScope(CorePackage.JV) {

    object Util : CoreScope(CorePackage.JV_UTIL) {

        val C_ArrayList = CoreClassDeclaration(parent, "ArrayList", Core.Kt.C_Any, Target.C_ArrayList)

        val F_ArrayList = CoreConstructorDeclaration(C_ArrayList, Target.ArrayList.F_new)
    }
}
