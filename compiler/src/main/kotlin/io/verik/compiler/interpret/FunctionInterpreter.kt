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

import io.verik.compiler.ast.element.common.ECallExpression
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.ENullElement
import io.verik.compiler.ast.element.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.sv.*
import io.verik.compiler.ast.property.FunctionAnnotationType
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.m

object FunctionInterpreter {

    fun interpret(function: EKtFunction): EElement {
        val body = function.body
        return when (function.annotationType) {
            FunctionAnnotationType.RUN -> {
                if (body != null) {
                    EInitialBlock(function.location, function.name, body)
                } else {
                    m.error("Run block missing body: ${function.name}", function)
                    ENullElement(function.location)
                }
            }
            FunctionAnnotationType.COM -> {
                if (body != null) {
                    EAlwaysComBlock(function.location, function.name, body)
                } else {
                    m.error("Com block missing body: ${function.name}", function)
                    ENullElement(function.location)
                }
            }
            FunctionAnnotationType.SEQ -> {
                if (body != null) {
                    getAlwaysSeqBlock(function, body)
                        ?: ENullElement(function.location)
                } else {
                    m.error("Seq block missing body: ${function.name}", function)
                    ENullElement(function.location)
                }
            }
            else -> {
                ESvFunction(
                    function.location,
                    function.name,
                    function.returnType,
                    function.body
                )
            }
        }
    }

    private fun getAlwaysSeqBlock(function: EKtFunction, body: EExpression): EAlwaysSeqBlock? {
        val onExpression = if (body is EKtBlockExpression) {
            if (body.statements.size == 1) {
                body.statements[0]
            } else  {
                m.error("On expression expected", body)
                return null
            }
        } else body
        if (onExpression !is ECallExpression || onExpression.reference != Core.Vk.ON_EVENT_FUNCTION) {
            m.error("On expression expected", body)
            return null
        }
        val eventExpression = onExpression.valueArguments[0].expression
        val eventControlExpression = EEventControlExpression(eventExpression.location, eventExpression)
        val alwaysSeqBody = onExpression.valueArguments[1].expression
            .cast<EFunctionLiteralExpression>()?.body
            ?: return null
        return EAlwaysSeqBlock(
            function.location,
            function.name,
            alwaysSeqBody,
            eventControlExpression
        )
    }
}