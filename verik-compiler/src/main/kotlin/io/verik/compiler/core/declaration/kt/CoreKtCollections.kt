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

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.kt.EFunctionLiteralExpression
import io.verik.compiler.core.common.BasicCoreFunctionDeclaration
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.resolve.TypeAdapter
import io.verik.compiler.resolve.TypeConstraint
import io.verik.compiler.resolve.TypeConstraintKind

object CoreKtCollections : CoreScope(CorePackage.KT_COLLECTIONS) {

    val F_forEach_Function = object : BasicCoreFunctionDeclaration(
        parent,
        "forEach",
        "fun forEach(Function)",
        null
    ) {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            val functionLiteralExpression = callExpression.valueArguments[0] as EFunctionLiteralExpression
            if (callExpression.receiver!!.type.arguments.isEmpty()) return listOf()
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.EQ_IN,
                    TypeAdapter.ofTypeArgument(callExpression, 0),
                    TypeAdapter.ofElement(callExpression.receiver!!, 0)
                ),
                TypeConstraint(
                    TypeConstraintKind.EQ_IN,
                    TypeAdapter.ofElement(functionLiteralExpression.valueParameters[0]),
                    TypeAdapter.ofElement(callExpression.receiver!!, 0)
                )
            )
        }
    }
}
