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

import verikc.alx.AlxRuleIndex
import verikc.alx.AlxTerminalIndex
import verikc.alx.AlxTree
import verikc.base.ast.LineException

object KtParserTypeIdentifier {

    fun parse(type: AlxTree): String {
        return when (type.index) {
            AlxRuleIndex.TYPE -> {
                val child = type.unwrap()
                when (child.index) {
                    AlxRuleIndex.PARENTHESIZED_TYPE -> {
                        parse(child.find(AlxRuleIndex.TYPE))
                    }
                    AlxRuleIndex.TYPE_REFERENCE -> {
                        parse(child.find(AlxRuleIndex.USER_TYPE))
                    }
                    else -> throw LineException(
                        "parenthesized type or type reference expected",
                        type.line
                    )
                }
            }
            AlxRuleIndex.USER_TYPE -> {
                val simpleUserTypes = type.findAll(AlxRuleIndex.SIMPLE_USER_TYPE)
                if (simpleUserTypes.size != 1) {
                    throw LineException("fully qualified type references not supported", type.line)
                }
                simpleUserTypes[0]
                    .find(AlxRuleIndex.SIMPLE_IDENTIFIER)
                    .find(AlxTerminalIndex.IDENTIFIER).text!!
            }
            else -> throw LineException("type or user type expected", type.line)
        }
    }
}
