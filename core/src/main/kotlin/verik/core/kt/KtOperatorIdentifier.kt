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

import verik.core.main.Line
import verik.core.main.LineException

enum class KtOperatorIdentifier {
    OR,
    AND,
    LT,
    GT,
    LT_EQ,
    GT_EQ,
    IN,
    NOT_IN,
    RANGE,
    ADD,
    SUB,
    MUL,
    MOD,
    DIV,
    UNARY_ADD,
    UNARY_SUB,
    NOT,
    GET,
    IF,
    IF_ELSE,
    RETURN_UNIT,
    RETURN,
    CONTINUE,
    BREAK,
    INFIX_WITH,
    INFIX_CON,
    INFIX_PUT,
    INFIX_REG,
    LAMBDA_ON,
    LAMBDA_FOREVER;

    companion object {

        fun infixIdentifier(identifier: String, line: Line): KtOperatorIdentifier {
            return when (identifier) {
                "with" -> INFIX_WITH
                "con" -> INFIX_CON
                "put" -> INFIX_PUT
                "reg" -> INFIX_REG
                else -> throw LineException("infix operator $identifier not supported", line)
            }
        }

        fun lambdaIdentifier(identifier: String, line: Line): KtOperatorIdentifier {
            return when (identifier) {
                "on" -> LAMBDA_ON
                "forever" -> LAMBDA_FOREVER
                else -> throw LineException("lambda operator $identifier not supported", line)
            }
        }
    }
}