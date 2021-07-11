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

package io.verik.compiler.interpret

import io.verik.compiler.ast.element.common.EAbstractBlockExpression
import io.verik.compiler.ast.element.common.ECallExpression
import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.cast
import io.verik.compiler.ast.element.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.sv.*
import io.verik.compiler.ast.property.FunctionAnnotationType
import io.verik.compiler.core.Core
import io.verik.compiler.main.m

object FunctionInterpreter {

    fun interpret(function: EKtFunction): EDeclaration? {
        val bodyBlockExpression = function.bodyBlockExpression
        return when (function.annotationType) {
            FunctionAnnotationType.RUN -> {
                if (bodyBlockExpression != null) {
                    EInitialBlock(
                        function.location,
                        function.name,
                        bodyBlockExpression
                    )
                } else {
                    m.error("Run block missing body: $function", function)
                    null
                }
            }
            FunctionAnnotationType.COM -> {
                if (bodyBlockExpression != null) {
                    EAlwaysComBlock(
                        function.location,
                        function.name,
                        bodyBlockExpression
                    )
                } else {
                    m.error("Com block missing body: $function", function)
                    null
                }
            }
            FunctionAnnotationType.SEQ -> {
                if (bodyBlockExpression != null) {
                    getAlwaysSeqBlock(function, bodyBlockExpression)
                } else {
                    m.error("Seq block missing body: $function", function)
                    null
                }
            }
            else -> {
                ESvFunction(
                    function.location,
                    function.name,
                    function.type,
                    function.bodyBlockExpression
                )
            }
        }
    }

    private fun getAlwaysSeqBlock(
        function: EKtFunction,
        bodyBlockExpression: EAbstractBlockExpression
    ): EAlwaysSeqBlock? {
        if (bodyBlockExpression.statements.size != 1) {
            m.error("On expression expected", bodyBlockExpression)
            return null
        }
        val onExpression = bodyBlockExpression.statements[0]
        if (onExpression !is ECallExpression || onExpression.reference != Core.Vk.ON_EVENT_FUNCTION) {
            m.error("On expression expected", bodyBlockExpression)
            return null
        }
        val eventExpression = onExpression.valueArguments[0].expression
        val eventControlExpression = EEventControlExpression(eventExpression.location, eventExpression)
        val alwaysSeqBlockBodyBlockExpression = onExpression.valueArguments[1].expression
            .cast<EFunctionLiteralExpression>()?.bodyBlockExpression
            ?: return null
        return EAlwaysSeqBlock(
            function.location,
            function.name,
            alwaysSeqBlockBodyBlockExpression,
            eventControlExpression
        )
    }
}