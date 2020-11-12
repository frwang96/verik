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

package verik.core.ps.extract

import verik.core.base.ast.BaseType
import verik.core.base.ast.Line
import verik.core.base.ast.LineException
import verik.core.base.ast.ReifiedType
import verik.core.lang.LangSymbol
import verik.core.ps.ast.PsExpressionString
import verik.core.ps.ast.PsStringSegment
import verik.core.ps.ast.PsStringSegmentExpression
import verik.core.ps.ast.PsStringSegmentLiteral
import verik.core.ps.symbol.PsSymbolTable
import verik.core.sv.ast.SvExpression
import verik.core.sv.ast.SvExpressionFunction
import verik.core.sv.ast.SvExpressionLiteral

object PsExpressionExtractorString {

    fun extract(string: PsExpressionString, symbolTable: PsSymbolTable): SvExpression {
        return if (string.segments.all { it is PsStringSegmentLiteral }) {
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
                if (it is PsStringSegmentExpression) {
                    it.expression.extract(symbolTable)
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

    fun defaultFormatString(reifiedType: ReifiedType, line: Line): String {
        return when(reifiedType.type) {
            LangSymbol.TYPE_BOOL -> "%b"
            LangSymbol.TYPE_INT, LangSymbol.TYPE_UINT, LangSymbol.TYPE_SINT -> "%0d"
            else -> throw LineException("formatting of expression not supported", line)
        }
    }

    private fun formatString(segment: PsStringSegment): String {
        return when (segment) {
            is PsStringSegmentLiteral -> {
                segment.string.replace("%", "%%")
            }
            is PsStringSegmentExpression -> {
                val reifiedType = segment.expression.reifiedType
                when (segment.baseType) {
                    BaseType.DEFAULT -> defaultFormatString(reifiedType, segment)
                    BaseType.BIN -> {
                        if (reifiedType.type !in listOf(LangSymbol.TYPE_BOOL, LangSymbol.TYPE_INT, LangSymbol.TYPE_UINT, LangSymbol.TYPE_SINT)) {
                            throw LineException("expression cannot be formated in binary", segment)
                        }
                        "%b"
                    }
                    BaseType.HEX -> {
                        if (reifiedType.type !in listOf(LangSymbol.TYPE_BOOL, LangSymbol.TYPE_INT, LangSymbol.TYPE_UINT, LangSymbol.TYPE_SINT)) {
                            throw LineException("expression cannot be formated in hexadecimal", segment)
                        }
                        "%h"
                    }
                }
            }
        }
    }
}
