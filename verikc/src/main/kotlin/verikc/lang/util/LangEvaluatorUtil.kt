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

package verikc.lang.util

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.FUNCTION_EXP_INT
import verikc.lang.LangSymbol.FUNCTION_LOG_INT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ADD_INT_INT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_DIV_INT_INT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_MUL_INT_INT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_REM_INT_INT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_SUB_INT_INT

object LangEvaluatorUtil {

    fun evaluate(functionSymbol: Symbol, receiverEvaluated: Int?, argsEvaluated: List<Int>, line: Line): Int? {
        return when (functionSymbol) {
            FUNCTION_NATIVE_ADD_INT_INT -> {
                receiverEvaluated!! + argsEvaluated[0]
            }
            FUNCTION_NATIVE_SUB_INT_INT -> {
                receiverEvaluated!! - argsEvaluated[0]
            }
            FUNCTION_NATIVE_MUL_INT_INT -> {
                receiverEvaluated!! * argsEvaluated[0]
            }
            FUNCTION_NATIVE_DIV_INT_INT -> {
                receiverEvaluated!! / argsEvaluated[0]
            }
            FUNCTION_NATIVE_REM_INT_INT -> {
                receiverEvaluated!! % argsEvaluated[0]
            }
            FUNCTION_LOG_INT -> {
                val value = argsEvaluated[0]
                if (value <= 0) throw LineException("illegal argument $value to log function", line)
                if (value == 1) 1
                else 32 - (value - 1).countLeadingZeroBits()
            }
            FUNCTION_EXP_INT -> {
                val value = argsEvaluated[0]
                if (value < 0 || value >= 31)
                    throw LineException("illegal argument $value to exp function", line)
                1 shl value
            }
            else -> null
        }
    }
}