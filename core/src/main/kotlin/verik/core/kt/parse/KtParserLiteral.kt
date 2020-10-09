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

package verik.core.kt.parse

import verik.core.al.AlRule
import verik.core.base.Line
import verik.core.base.LineException
import verik.core.base.LiteralValue
import verik.core.kt.KtExpressionLiteral
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_INT

object KtParserLiteral {

    fun parse(literalConstant: AlRule): KtExpressionLiteral {
        val string = literalConstant.firstAsTokenText()
        return when {
            string == "true" -> KtExpressionLiteral(literalConstant.line, TYPE_BOOL, LiteralValue.fromBoolean(true))
            string == "false" -> KtExpressionLiteral(literalConstant.line, TYPE_BOOL, LiteralValue.fromBoolean(false))
            string.getOrNull(1) in listOf('b', 'B') -> parseBin(string, literalConstant)
            string.getOrNull(1) in listOf('x', 'X') -> parseHex(string, literalConstant)
            else -> parseInt(string, literalConstant)
        }
    }

    private fun parseBin(string: String, line: Line): KtExpressionLiteral {
        val strippedString = string.substring(2).replace("_", "")
        val value = strippedString.toIntOrNull(2)
                ?: throw LineException("unable to parse binary literal $string", line)
        return KtExpressionLiteral(
                line.line,
                TYPE_INT,
                LiteralValue.fromInt(value)
        )
    }

    private fun parseHex(string: String, line: Line): KtExpressionLiteral {
        val strippedString = string.substring(2).replace("_", "")
        val value = strippedString.toIntOrNull(16)
                ?: throw LineException("unable to parse hexadecimal literal $string", line)
        return KtExpressionLiteral(
                line.line,
                TYPE_INT,
                LiteralValue.fromInt(value)
        )
    }

    private fun parseInt(string: String, line: Line): KtExpressionLiteral {
        val strippedString = string.replace("_", "")
        val value = strippedString.toIntOrNull()
                ?: throw LineException("unable to parse integer literal $string", line)
        return KtExpressionLiteral(
                line.line,
                TYPE_INT,
                LiteralValue.fromInt(value)
        )
    }
}