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

package verik.core.kt

import verik.core.al.AlRule
import verik.core.al.AlRuleType
import verik.core.base.LineException

object KtTypeIdentifierParser {

    fun parse(type: AlRule): String {
        return when (type.type) {
            AlRuleType.TYPE -> {
                val child = type.firstAsRule()
                when (child.type) {
                    AlRuleType.PARENTHESIZED_TYPE -> {
                        parse(child.childAs(AlRuleType.TYPE))
                    }
                    AlRuleType.TYPE_REFERENCE -> {
                        parse(child.childAs(AlRuleType.USER_TYPE))
                    }
                    AlRuleType.FUNCTION_TYPE -> {
                        throw LineException("function type not supported", type)
                    }
                    else -> throw LineException("parenthesized type or type reference or function type expected", type)
                }
            }
            AlRuleType.USER_TYPE -> {
                val simpleUserTypes = type.children
                if (simpleUserTypes.size != 1) {
                    throw LineException("fully qualified type references not supported", type)
                }
                val simpleUserType = simpleUserTypes[0].asRule()
                if (simpleUserType.containsType(AlRuleType.TYPE_ARGUMENTS)) {
                    throw LineException("type arguments not supported", type)
                }
                simpleUserType.childAs(AlRuleType.SIMPLE_IDENTIFIER).firstAsTokenText()
            }
            else -> throw LineException("type or user type expected", type)
        }
    }
}