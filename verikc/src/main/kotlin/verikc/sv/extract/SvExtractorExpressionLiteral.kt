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

import verikc.base.ast.LineException
import verikc.lang.LangSymbol.TYPE_BOOLEAN
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.ps.ast.PsExpressionLiteral
import verikc.sv.ast.SvExpressionLiteral

object SvExtractorExpressionLiteral {

    fun extract(literal: PsExpressionLiteral): SvExpressionLiteral {
        val string = when (literal.typeGenerified.typeSymbol) {
            TYPE_BOOLEAN -> stringFromBool(literal)
            TYPE_INT -> stringFromInt(literal)
            TYPE_UBIT -> stringFromUbit(literal)
            TYPE_SBIT -> stringFromSbit(literal)
            else -> throw LineException("extraction of literal not supported", literal.line)
        }
        return SvExpressionLiteral(literal.line, string)
    }

    private fun stringFromBool(literal: PsExpressionLiteral): String {
        return if (literal.value.toBoolean()) "1'b1"
        else "1'b0"
    }

    private fun stringFromInt(literal: PsExpressionLiteral): String {
        return literal.value.toInt().toString()
    }

    private fun stringFromUbit(literal: PsExpressionLiteral): String {
        return "${literal.value.width}'h${literal.value.hexString()}"
    }

    private fun stringFromSbit(literal: PsExpressionLiteral): String {
        return "${literal.value.width}'sh${literal.value.hexString()}"
    }
}
