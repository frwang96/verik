/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.compiler.specialize

import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EConstantExpression
import io.verik.compiler.ast.element.expression.kt.EFunctionLiteralExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Cardinal
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.Messages

object OptionalReducerSubstage : SpecializerSubstage() {

    override fun process(declaration: EDeclaration, typeParameterBinding: TypeParameterBinding) {
        declaration.accept(OptionalReducerVisitor)
    }

    private object OptionalReducerVisitor : TreeVisitor() {

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            if (callExpression.reference == Core.Vk.F_optional_Function) {
                val property = callExpression.parent
                if (property !is EProperty) {
                    Messages.OPTIONAL_NOT_DIRECT_ASSIGNMENT.on(callExpression)
                    return
                }
                if (property.isMutable) {
                    Messages.OPTIONAL_NOT_VAL.on(callExpression)
                }

                val typeArgument = callExpression.typeArguments[0]
                when (typeArgument.reference) {
                    Cardinal.TRUE -> {
                        val functionLiteralExpression = callExpression
                            .valueArguments[0]
                            .cast<EFunctionLiteralExpression>()
                        if (functionLiteralExpression.body.statements.size == 1) {
                            callExpression.replace(functionLiteralExpression.body.statements[0])
                            return
                        }
                    }
                    Cardinal.FALSE -> {
                        val constantExpression = EConstantExpression(
                            callExpression.location,
                            Core.Kt.C_Nothing.toType(),
                            "null"
                        )
                        callExpression.replace(constantExpression)
                        property.type = constantExpression.type.copy()
                        return
                    }
                }
                Messages.CARDINAL_NOT_BOOLEAN.on(callExpression, typeArgument)
            }
        }
    }
}
