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

import io.verik.core.FileLineException
import io.verik.core.al.AlRule
import io.verik.core.al.AlRuleType

data class KtType(val pkg: KtPkgIdentifier, val identifier: String) {

    companion object {

        fun identifier(type: AlRule): String {
            return when (type.type) {
                AlRuleType.TYPE -> {
                    val child = type.firstAsRule()
                    when (child.type) {
                        AlRuleType.PARENTHESIZED_TYPE -> {
                            identifier(child.childAs(AlRuleType.TYPE))
                        }
                        AlRuleType.TYPE_REFERENCE -> {
                            identifier(child.childAs(AlRuleType.USER_TYPE))
                        }
                        AlRuleType.FUNCTION_TYPE -> {
                            throw FileLineException("function type not supported", type.fileLine)
                        }
                        else -> throw FileLineException("parenthesized type or type reference or function type expected", type.fileLine)
                    }
                }
                AlRuleType.USER_TYPE -> {
                    val simpleUserTypes = type.children
                    if (simpleUserTypes.size != 1) {
                        throw FileLineException("fully qualified type references not supported", type.fileLine)
                    }
                    val simpleUserType = simpleUserTypes[0].asRule()
                    if (simpleUserType.containsType(AlRuleType.TYPE_ARGUMENTS)) {
                        throw FileLineException("type not supported", type.fileLine)
                    }
                    simpleUserType.childAs(AlRuleType.SIMPLE_IDENTIFIER).firstAsTokenText()
                }
                else -> throw FileLineException("type or user type expected", type.fileLine)
            }
        }
    }
}