/*
 * Copyright 2020 Francis Wang
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

package io.verik.core.kt

import io.verik.core.Line
import io.verik.core.LineException
import io.verik.core.al.AlRule
import io.verik.core.al.AlRuleType
import io.verik.core.kt.symbol.KtSymbolType

data class KtParameter(
        override val line: Int,
        val identifier: String,
        val typeIdentifier: String,
        val expression: KtExpression?,
        val vararg: Boolean,
        var type: KtSymbolType?
): Line {

    companion object {

        operator fun invoke(parameter: AlRule): KtParameter {
            return when (parameter.type) {
                AlRuleType.CLASS_PARAMETER -> {
                    val identifier = parameter.childAs(AlRuleType.SIMPLE_IDENTIFIER).firstAsTokenText()
                    val typeIdentifier = KtTypeIdentifierParser.parse(parameter.childAs(AlRuleType.TYPE))
                    val expression = if (parameter.containsType(AlRuleType.EXPRESSION)) {
                        KtExpression(parameter.childAs(AlRuleType.EXPRESSION))
                    } else null
                    KtParameter(parameter.line, identifier, typeIdentifier, expression, false, null)
                }
                AlRuleType.FUNCTION_VALUE_PARAMETER -> {
                    val identifier = parameter
                            .childAs(AlRuleType.PARAMETER)
                            .childAs(AlRuleType.SIMPLE_IDENTIFIER)
                            .firstAsTokenText()
                    val typeIdentifier = KtTypeIdentifierParser.parse(parameter
                            .childAs(AlRuleType.PARAMETER)
                            .childAs(AlRuleType.TYPE))
                    val expression = if (parameter.containsType(AlRuleType.EXPRESSION)) {
                        KtExpression(parameter.childAs(AlRuleType.EXPRESSION))
                    } else null
                    KtParameter(parameter.line, identifier, typeIdentifier, expression, false, null)
                }
                else -> throw LineException("class parameter or function value parameter expected", parameter)
            }
        }
    }
}