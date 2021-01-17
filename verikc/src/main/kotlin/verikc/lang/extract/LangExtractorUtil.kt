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

package verikc.lang.extract

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.TypeGenerified
import verikc.lang.LangSymbol
import verikc.lang.LangSymbol.TYPE_INT
import verikc.ps.ast.PsExpression
import verikc.ps.ast.PsExpressionLiteral

object LangExtractorUtil {

    fun defaultFormatString(typeGenerified: TypeGenerified, line: Line): String {
        return when (typeGenerified.typeSymbol) {
            LangSymbol.TYPE_BOOL -> "%b"
            TYPE_INT, LangSymbol.TYPE_UBIT, LangSymbol.TYPE_SBIT -> "%0d"
            LangSymbol.TYPE_TIME -> "%0t"
            else -> throw LineException("formatting of expression not supported", line)
        }
    }

    fun intLiteralToInt(expression: PsExpression): Int {
        return if (expression is PsExpressionLiteral && expression.typeGenerified == TYPE_INT.toTypeGenerified()) {
            expression.value.toInt()
        } else throw LineException("expected int literal", expression.line)
    }

    fun widthToPacked(width: Int): String {
        return "[${width - 1}:0]"
    }
}
