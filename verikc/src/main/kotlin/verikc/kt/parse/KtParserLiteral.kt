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

import verikc.al.AlTerminal
import verikc.al.AlTree
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.LiteralValue
import verikc.kt.ast.KtExpressionLiteral
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_INT

object KtParserLiteral {

    fun parse(literalConstant: AlTree): KtExpressionLiteral {
        val child = literalConstant.unwrap()
        val string = child.text
        return when (child.index) {
            AlTerminal.BOOLEAN_LITERAL -> {
                when (string) {
                    "true" -> KtExpressionLiteral(literalConstant.line, TYPE_BOOL, LiteralValue.fromBoolean(true))
                    "false" -> KtExpressionLiteral(literalConstant.line, TYPE_BOOL, LiteralValue.fromBoolean(false))
                    else -> throw LineException("expected boolean literal", child.line)
                }
            }
            AlTerminal.INTEGER_LITERAL -> parseInt(string, child.line)
            AlTerminal.HEX_LITERAL -> parseHex(string, child.line)
            AlTerminal.BIN_LITERAL -> parseBin(string, child.line)
            else -> throw LineException("literal constant not recognized", child.line)
        }
    }

    private fun parseBin(string: String, line: Line): KtExpressionLiteral {
        val strippedString = string.substring(2).replace("_", "")
        val value = strippedString.toIntOrNull(2)
            ?: throw LineException("unable to parse binary literal $string", line)
        return KtExpressionLiteral(
            line,
            TYPE_INT,
            LiteralValue.fromInt(value)
        )
    }

    private fun parseHex(string: String, line: Line): KtExpressionLiteral {
        val strippedString = string.substring(2).replace("_", "")
        val value = strippedString.toIntOrNull(16)
            ?: throw LineException("unable to parse hexadecimal literal $string", line)
        return KtExpressionLiteral(
            line,
            TYPE_INT,
            LiteralValue.fromInt(value)
        )
    }

    private fun parseInt(string: String, line: Line): KtExpressionLiteral {
        val strippedString = string.replace("_", "")
        val value = strippedString.toIntOrNull()
            ?: throw LineException("unable to parse integer literal $string", line)
        return KtExpressionLiteral(
            line,
            TYPE_INT,
            LiteralValue.fromInt(value)
        )
    }
}
