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

package verik.core.it

import verik.core.it.symbol.ItSymbolTable
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_SINT
import verik.core.lang.LangSymbol.TYPE_UINT
import verik.core.main.LineException
import verik.core.sv.SvExpression
import verik.core.sv.SvExpressionFunction
import verik.core.sv.SvExpressionLiteral

object ItExpressionExtractorString {

    fun extract(string: ItExpressionString, symbolTable: ItSymbolTable): SvExpression {
        return if (string.segments.all { it is ItStringSegmentLiteral }) {
            val strings = string.segments.map { format(it) }
            SvExpressionLiteral(
                    string.line,
                    "\"${strings.joinToString(separator = "")}\""
            )
        } else {
            val strings = string.segments.map { format(it) }
            val stringLiteral = SvExpressionLiteral(
                    string.line,
                    "\"${strings.joinToString(separator = "")}\""
            )
            val expressions = string.segments.mapNotNull {
                if (it is ItStringSegmentExpression) {
                    ItExpressionExtractor.extract(it.expression, symbolTable)
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

    private fun format(segment: ItStringSegment): String {
        return when (segment) {
            is ItStringSegmentLiteral -> {
                segment.string.replace("%", "%%")
            }
            is ItStringSegmentExpression -> {
                val type = segment.expression.typeReified
                        ?: throw LineException("expression has not been reified", segment.expression)
                if (type.type !in listOf(TYPE_INT, TYPE_BOOL, TYPE_UINT, TYPE_SINT)) {
                    throw LineException("string formatting of expression not supported", segment.expression)
                }
                when (segment.base) {
                    ItStringSegmentExpressionBase.BIN -> "%b"
                    ItStringSegmentExpressionBase.DEC -> "%0d"
                    ItStringSegmentExpressionBase.HEX -> "%h"
                }
            }
        }
    }
}