/*
 * Copyright (c) 2020 Francis Wang
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

package verikc.sv.extract

import verikc.base.ast.BaseType
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.TypeGenerified
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.ps.ast.PsExpressionString
import verikc.ps.ast.PsStringSegment
import verikc.ps.ast.PsStringSegmentExpression
import verikc.ps.ast.PsStringSegmentLiteral
import verikc.sv.ast.SvExpression
import verikc.sv.ast.SvExpressionFunction
import verikc.sv.ast.SvExpressionLiteral
import verikc.sv.table.SvSymbolTable

object SvExtractorExpressionString {

    fun extract(string: PsExpressionString, symbolTable: SvSymbolTable): SvExpression {
        return if (string.segments.all { it is PsStringSegmentLiteral }) {
            val strings = string.segments.map { formatString(it) }
            SvExpressionLiteral(string.line, "\"${strings.joinToString(separator = "")}\"")
        } else {
            val strings = string.segments.map { formatString(it) }
            val stringLiteral = SvExpressionLiteral(string.line, "\"${strings.joinToString(separator = "")}\"")
            val expressions = string.segments.mapNotNull {
                if (it is PsStringSegmentExpression) {
                    SvExtractorExpressionBase.extract(it.expression, symbolTable)
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

    fun defaultFormatString(typeGenerified: TypeGenerified, line: Line): String {
        return when (typeGenerified.typeSymbol) {
            TYPE_BOOL -> "%b"
            TYPE_INT, TYPE_UBIT, TYPE_SBIT -> "%0d"
            else -> throw LineException("formatting of expression not supported", line)
        }
    }

    private fun formatString(segment: PsStringSegment): String {
        return when (segment) {
            is PsStringSegmentLiteral -> {
                segment.string.replace("%", "%%")
            }
            is PsStringSegmentExpression -> {
                val typeGenerified = segment.expression.typeGenerified
                when (segment.baseType) {
                    BaseType.DEFAULT -> defaultFormatString(typeGenerified, segment.line)
                    BaseType.BIN -> {
                        if (typeGenerified.typeSymbol !in listOf(TYPE_BOOL, TYPE_INT, TYPE_UBIT, TYPE_SBIT)) {
                            throw LineException("expression cannot be formatted in binary", segment.line)
                        }
                        "%b"
                    }
                    BaseType.HEX -> {
                        if (typeGenerified.typeSymbol !in listOf(TYPE_BOOL, TYPE_INT, TYPE_UBIT, TYPE_SBIT)) {
                            throw LineException("expression cannot be formatted in hexadecimal", segment.line)
                        }
                        "%h"
                    }
                }
            }
        }
    }
}
