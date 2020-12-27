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

package verikc.kt.parse

import verikc.al.ast.AlRule
import verikc.al.ast.AlTree
import verikc.base.ast.LineException

object KtParserTypeIdentifier {

    fun parseType(type: AlTree): String {
        val child = type.unwrap()
        return when (child.index) {
            AlRule.PARENTHESIZED_TYPE -> {
                parseType(child.find(AlRule.TYPE))
            }
            AlRule.TYPE_REFERENCE -> {
                parseUserType(child.find(AlRule.USER_TYPE))
            }
            else -> throw LineException(
                "parenthesized type or type reference expected",
                type.line
            )
        }
    }

    fun parseUserType(userType: AlTree): String {
        val simpleUserTypes = userType.findAll(AlRule.SIMPLE_USER_TYPE)
        if (simpleUserTypes.size != 1) {
            throw LineException("fully qualified type references not supported", userType.line)
        }
        return simpleUserTypes[0]
            .find(AlRule.SIMPLE_IDENTIFIER)
            .unwrap().text
    }
}
