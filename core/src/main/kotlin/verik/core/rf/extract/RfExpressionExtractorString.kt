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

package verik.core.rf.extract

import verik.core.base.Line
import verik.core.base.LineException
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_SINT
import verik.core.lang.LangSymbol.TYPE_UINT
import verik.core.rf.ast.*
import verik.core.rf.symbol.RfSymbolTable
import verik.core.sv.ast.SvExpression
import verik.core.sv.ast.SvExpressionFunction
import verik.core.sv.ast.SvExpressionLiteral

object RfExpressionExtractorString {

    fun extract(string: RfExpressionString, symbolTable: RfSymbolTable): SvExpression {
        return if (string.segments.all { it is RfStringSegmentLiteral }) {
            val strings = string.segments.map { formatString(it) }
            SvExpressionLiteral(
                    string.line,
                    "\"${strings.joinToString(separator = "")}\""
            )
        } else {
            val strings = string.segments.map { formatString(it) }
            val stringLiteral = SvExpressionLiteral(
                    string.line,
                    "\"${strings.joinToString(separator = "")}\""
            )
            val expressions = string.segments.mapNotNull {
                if (it is RfStringSegmentExpression) {
                    it.expression.extractAsExpression(symbolTable)
                } else null
            }
            return SvExpressionFunction(
                    string.line,
                    null,
                    "\$sformatf",
                    listOf(stringLiteral) + expressions
            )
        }
    }

    fun defaultFormatString(reifiedType: RfReifiedType, line: Line): String {
        return when(reifiedType.type) {
            TYPE_BOOL -> "%b"
            TYPE_INT, TYPE_UINT, TYPE_SINT -> "%0d"
            else -> throw LineException("formatting of expression not supported", line)
        }
    }

    private fun formatString(segment: RfStringSegment): String {
        return when (segment) {
            is RfStringSegmentLiteral -> {
                segment.string.replace("%", "%%")
            }
            is RfStringSegmentExpression -> {
                val reifiedType = segment.expression.reifiedType
                        ?: throw LineException("expression has not been reified", segment.expression)

                when (segment.base) {
                    RfStringSegmentExpressionBase.DEFAULT -> defaultFormatString(reifiedType, segment)
                    RfStringSegmentExpressionBase.BIN -> {
                        if (reifiedType.type !in listOf(TYPE_BOOL, TYPE_INT, TYPE_UINT, TYPE_SINT)) {
                            throw LineException("expression cannot be formated in binary", segment)
                        }
                        "%b"
                    }
                    RfStringSegmentExpressionBase.HEX -> {
                        if (reifiedType.type !in listOf(TYPE_BOOL, TYPE_INT, TYPE_UINT, TYPE_SINT)) {
                            throw LineException("expression cannot be formated in hexadecimal", segment)
                        }
                        "%h"
                    }
                }
            }
        }
    }
}