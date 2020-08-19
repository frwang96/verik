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

package verik.core.vk

import verik.core.kt.KtOperatorIdentifier
import verik.core.base.LineException

enum class VkOperatorIdentifier {
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
    INFIX_PUT,
    INFIX_REG,
    LAMBDA_FOREVER;

    companion object {

        operator fun invoke(identifier: KtOperatorIdentifier, line: Int): VkOperatorIdentifier {
            return when (identifier) {
                KtOperatorIdentifier.OR -> OR
                KtOperatorIdentifier.AND -> AND
                KtOperatorIdentifier.LT -> LT
                KtOperatorIdentifier.GT -> GT
                KtOperatorIdentifier.LT_EQ -> LT_EQ
                KtOperatorIdentifier.GT_EQ -> GT_EQ
                KtOperatorIdentifier.IN -> IN
                KtOperatorIdentifier.NOT_IN -> NOT_IN
                KtOperatorIdentifier.RANGE -> RANGE
                KtOperatorIdentifier.ADD -> ADD
                KtOperatorIdentifier.SUB -> SUB
                KtOperatorIdentifier.MUL -> MUL
                KtOperatorIdentifier.MOD -> MOD
                KtOperatorIdentifier.DIV -> DIV
                KtOperatorIdentifier.UNARY_ADD -> UNARY_ADD
                KtOperatorIdentifier.UNARY_SUB -> UNARY_SUB
                KtOperatorIdentifier.NOT -> NOT
                KtOperatorIdentifier.GET -> GET
                KtOperatorIdentifier.IF -> IF
                KtOperatorIdentifier.IF_ELSE -> IF_ELSE
                KtOperatorIdentifier.RETURN_UNIT -> RETURN_UNIT
                KtOperatorIdentifier.RETURN -> RETURN
                KtOperatorIdentifier.CONTINUE -> CONTINUE
                KtOperatorIdentifier.BREAK -> BREAK
                KtOperatorIdentifier.INFIX_WITH -> throw LineException("illegal use of with expression", line)
                KtOperatorIdentifier.INFIX_CON -> throw LineException("illegal use of con expression", line)
                KtOperatorIdentifier.INFIX_PUT -> INFIX_PUT
                KtOperatorIdentifier.INFIX_REG -> INFIX_REG
                KtOperatorIdentifier.LAMBDA_ON -> throw LineException("illegal use of on expression", line)
                KtOperatorIdentifier.LAMBDA_FOREVER -> LAMBDA_FOREVER
            }
        }
    }
}
