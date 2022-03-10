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

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.core.common.BasicCoreFunctionDeclaration
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CorePropertyDeclaration
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.resolve.TypeAdapter
import io.verik.compiler.resolve.TypeConstraint
import io.verik.compiler.resolve.TypeConstraintKind
import io.verik.compiler.target.common.Target

object CoreJvArrayList : CoreScope(Core.Jv.Util.C_ArrayList) {

    val F_add_E = object : BasicCoreFunctionDeclaration(parent, "add", "fun add(E)", Target.ArrayList.F_add) {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.EQ_IN,
                    TypeAdapter.ofElement(callExpression.valueArguments[0]),
                    TypeAdapter.ofElement(callExpression.receiver!!, 0)
                )
            )
        }
    }

    val F_get_Int = object : BasicCoreFunctionDeclaration(parent, "get", "fun get(Int)", Target.ArrayList.F_get) {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.EQ_OUT,
                    TypeAdapter.ofElement(callExpression),
                    TypeAdapter.ofElement(callExpression.receiver!!, 0)
                )
            )
        }
    }

    val F_set_Int_E = object : BasicCoreFunctionDeclaration(parent, "set", "fun set(Int, E)", Target.ArrayList.F_set) {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.EQ_IN,
                    TypeAdapter.ofElement(callExpression.valueArguments[1]),
                    TypeAdapter.ofElement(callExpression.receiver!!, 0)
                )
            )
        }
    }

    val P_size = object : CorePropertyDeclaration(parent, "size") {

        override fun transform(referenceExpression: EReferenceExpression): EExpression {
            return ECallExpression(
                referenceExpression.location,
                referenceExpression.type,
                Target.ArrayList.F_size,
                referenceExpression.receiver,
                false,
                arrayListOf(),
                arrayListOf()
            )
        }
    }
}
