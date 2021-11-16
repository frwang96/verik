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

import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.core.common.BasicCoreFunctionDeclaration
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CorePropertyDeclaration
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.target.common.Target

object CoreJvArrayList : CoreScope(Core.Jv.Util.C_ArrayList) {

    val F_add_E = BasicCoreFunctionDeclaration(parent, "add", "fun add(E)", Target.ArrayList.F_add)

    val F_get_Int = BasicCoreFunctionDeclaration(parent, "get", "fun get(Int)", Target.ArrayList.F_get)

    val F_set_Int_E = BasicCoreFunctionDeclaration(parent, "set", "fun set(Int, E)", Target.ArrayList.F_set)

    val P_size = object : CorePropertyDeclaration(parent, "size") {

        override fun transform(referenceExpression: EReferenceExpression): EExpression {
            return EKtCallExpression(
                referenceExpression.location,
                referenceExpression.type,
                Target.ArrayList.F_size,
                referenceExpression.receiver,
                arrayListOf(),
                arrayListOf()
            )
        }
    }
}
