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

import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtUnaryExpression
import io.verik.compiler.ast.element.sv.EForStatement
import io.verik.compiler.ast.element.sv.ESvValueParameter
import io.verik.compiler.ast.property.KtUnaryOperatorKind
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.message.Messages

object CoreKtCollections : CoreScope(CorePackage.KT_COLLECTIONS) {

    val F_forEach_Function = object : TransformableCoreFunctionDeclaration(
        parent,
        "forEach",
        "fun forEach(Function)"
    ) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val functionLiteral = callExpression.valueArguments[0]
                .cast<EFunctionLiteralExpression>()
                ?: return callExpression
            val valueParameter = functionLiteral.valueParameters[0]
                .cast<ESvValueParameter>()
                ?: return callExpression
            val valueParameterReferenceExpression = EReferenceExpression(
                valueParameter.location,
                valueParameter.type.copy(),
                valueParameter,
                null
            )

            val receiver = callExpression.receiver!!
            return when {
                receiver is EKtCallExpression && receiver.reference == Core.Kt.Ranges.F_until_Int -> {
                    val initializer = receiver.receiver!!
                    val condition = EKtCallExpression(
                        receiver.location,
                        Core.Kt.C_Boolean.toType(),
                        Core.Kt.Int.F_lt_Int,
                        valueParameterReferenceExpression,
                        arrayListOf(receiver.valueArguments[0]),
                        ArrayList()
                    )
                    val iteration = EKtUnaryExpression(
                        receiver.location,
                        Core.Kt.C_Int.toType(),
                        ExpressionCopier.copy(valueParameterReferenceExpression),
                        KtUnaryOperatorKind.POST_INC
                    )
                    EForStatement(
                        receiver.location,
                        valueParameter,
                        initializer,
                        condition,
                        iteration,
                        functionLiteral.body
                    )
                }
                else -> {
                    Messages.INTERNAL_ERROR.on(callExpression, "Unable to transform call expression into for statement")
                    callExpression
                }
            }
        }
    }
}
