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

package io.verik.compiler.core.kt

import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtReferenceExpression
import io.verik.compiler.ast.element.kt.EKtUnaryExpression
import io.verik.compiler.ast.element.sv.EForStatement
import io.verik.compiler.ast.element.sv.ESvValueParameter
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.ast.property.KtUnaryOperatorKind
import io.verik.compiler.copy.ElementCopier
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreKtTransformableFunctionDeclaration
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.message.Messages

object CoreKtCollections : CoreScope(CorePackage.KT_COLLECTIONS) {

    val F_forEach_Function = object : CoreKtTransformableFunctionDeclaration(parent, "forEach", Core.Kt.C_Function) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            val functionLiteral = callExpression.valueArguments[0]
                .cast<EFunctionLiteralExpression>()
                ?: return callExpression
            val functionLiteralValueParameter = functionLiteral.valueParameters[0]
                .cast<ESvValueParameter>()
                ?: return callExpression
            val functionLiteralValueParameterReferenceExpression = EKtReferenceExpression(
                functionLiteralValueParameter.location,
                functionLiteralValueParameter.type.copy(),
                functionLiteralValueParameter,
                null
            )

            val receiver = callExpression.receiver!!
            if (receiver is EKtCallExpression && receiver.reference == Core.Kt.Ranges.F_until_Int) {
                val initializer = receiver.receiver!!
                val condition = EKtBinaryExpression(
                    receiver.location,
                    Core.Kt.C_Boolean.toType(),
                    functionLiteralValueParameterReferenceExpression,
                    receiver.valueArguments[0],
                    KtBinaryOperatorKind.LT
                )
                val iteration = EKtUnaryExpression(
                    receiver.location,
                    Core.Kt.C_Int.toType(),
                    ElementCopier.copy(functionLiteralValueParameterReferenceExpression),
                    KtUnaryOperatorKind.POST_INC
                )
                return EForStatement(
                    receiver.location,
                    functionLiteralValueParameter,
                    initializer,
                    condition,
                    iteration,
                    functionLiteral.body
                )
            } else {
                Messages.INTERNAL_ERROR.on(callExpression, "Unable to transform call expression into for statement")
                return callExpression
            }
        }
    }
}
