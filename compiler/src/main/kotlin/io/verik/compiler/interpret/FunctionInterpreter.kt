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

import io.verik.compiler.ast.element.common.CAbstractBlockExpression
import io.verik.compiler.ast.element.common.CCallExpression
import io.verik.compiler.ast.element.common.CDeclaration
import io.verik.compiler.ast.element.common.cast
import io.verik.compiler.ast.element.kt.KFunction
import io.verik.compiler.ast.element.kt.KFunctionLiteralExpression
import io.verik.compiler.ast.element.sv.*
import io.verik.compiler.ast.property.FunctionAnnotationType
import io.verik.compiler.core.CoreFunction
import io.verik.compiler.main.m

object FunctionInterpreter {

    fun interpret(function: KFunction): CDeclaration? {
        val bodyBlockExpression = function.bodyBlockExpression
        return when (function.annotationType) {
            FunctionAnnotationType.RUN -> {
                if (bodyBlockExpression != null) {
                    SInitialBlock(
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
                    SAlwaysComBlock(
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
                SFunction(
                    function.location,
                    function.name,
                    function.type,
                    function.bodyBlockExpression
                )
            }
        }
    }

    private fun getAlwaysSeqBlock(
        function: KFunction,
        bodyBlockExpression: CAbstractBlockExpression
    ): SAlwaysSeqBlock? {
        if (bodyBlockExpression.statements.size != 1) {
            m.error("On expression expected", bodyBlockExpression)
            return null
        }
        val onExpression = bodyBlockExpression.statements[0]
        if (onExpression !is CCallExpression || onExpression.reference != CoreFunction.Core.ON_EVENT_FUNCTION) {
            m.error("On expression expected", bodyBlockExpression)
            return null
        }
        val eventExpression = onExpression.valueArguments[0].expression
        val eventControlExpression = SEventControlExpression(eventExpression.location, eventExpression)
        val alwaysSeqBlockBodyBlockExpression = onExpression.valueArguments[1].expression
            .cast<KFunctionLiteralExpression>()?.bodyBlockExpression
            ?: return null
        return SAlwaysSeqBlock(
            function.location,
            function.name,
            alwaysSeqBlockBodyBlockExpression,
            eventControlExpression
        )
    }
}