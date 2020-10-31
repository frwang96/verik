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

import verik.core.base.ast.LineException
import verik.core.base.ast.LiteralValue
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_SINT
import verik.core.lang.LangSymbol.TYPE_UINT
import verik.core.ps.ast.PsExpressionLiteral
import verik.core.sv.ast.SvExpressionLiteral

// TODO remove annotation
@Suppress("DuplicatedCode")
object PsExpressionExtractorLiteral {

    fun extract(literal: PsExpressionLiteral): SvExpressionLiteral {
        val reifiedType = literal.reifiedType
        val string = when (reifiedType.type) {
            TYPE_BOOL -> stringFromBool(literal)
            TYPE_INT -> stringFromInt(literal)
            TYPE_UINT -> stringFromUint(literal, reifiedType.args)
            TYPE_SINT -> stringFromSint(literal, reifiedType.args)
            else -> throw LineException("extraction of literal not supported", literal)
        }
        return SvExpressionLiteral(
                literal.line,
                string
        )
    }

    private fun stringFromBool(literal: PsExpressionLiteral): String {
        return if (literal.value[0]) "1'b1"
        else "1'b0"
    }

    private fun stringFromInt(literal: PsExpressionLiteral): String {
        return literal.value.toInt().toString()
    }

    private fun stringFromUint(literal: PsExpressionLiteral, args: List<Int>): String {
        val width = args[0]
        val hexString = hexString(literal.value, width)
        return "$width'h$hexString"
    }

    private fun stringFromSint(literal: PsExpressionLiteral, args: List<Int>): String {
        val width = args[0]
        val hexString = hexString(literal.value, width)
        return "$width'sh$hexString"
    }

    private fun hexString(value: LiteralValue, width: Int): String {
        val length = Integer.max((width + 3) / 4, 1)
        val builder = StringBuilder()
        for (charPos in (length - 1) downTo 0) {
            builder.append(hexChar(value, charPos))
            if (charPos != 0 && (charPos % 4) == 0) {
                builder.append('_')
            }
        }
        return builder.toString()
    }

    private fun hexChar(value: LiteralValue, charPos: Int): Char {
        var code = 0
        for (index in 0 until 4) {
            val bitPos = (charPos * 4) + index
            val bit = if (bitPos >= value.width) false
            else value[bitPos]
            if (bit) {
                code = code or (1 shl index)
            }
        }
        return when {
            code < 10 -> '0' + code
            code < 16 -> 'a' + code - 10
            else -> throw IllegalArgumentException("char code out of range")
        }
    }
}
