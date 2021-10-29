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

package io.verik.compiler.core.declaration.jv

import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreClassDeclaration
import io.verik.compiler.core.common.CoreConstructorDeclaration
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.target.common.Target

object CoreJvClass : CoreScope(CorePackage.JV) {

    object Util : CoreScope(CorePackage.JV_UTIL) {

        val C_ArrayList = CoreClassDeclaration(parent, "ArrayList", Core.Kt.C_Any, Target.C_ArrayList)

        val F_ArrayList = CoreConstructorDeclaration(parent, C_ArrayList, Target.ArrayList.F_new)
    }
}
