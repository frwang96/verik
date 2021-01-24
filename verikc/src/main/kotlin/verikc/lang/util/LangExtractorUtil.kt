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

import verikc.base.ast.LineException
import verikc.base.ast.TypeGenerified
import verikc.lang.LangSymbol.TYPE_BOOLEAN
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_STRING
import verikc.lang.LangSymbol.TYPE_TIME
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.ps.ast.PsExpression
import verikc.ps.ast.PsExpressionLiteral

object LangExtractorUtil {

    fun defaultFormatString(typeGenerified: TypeGenerified): String {
        return when (typeGenerified.typeSymbol) {
            TYPE_BOOLEAN -> "%b"
            TYPE_INT, TYPE_UBIT, TYPE_SBIT -> "%0d"
            TYPE_TIME -> "%0t"
            TYPE_STRING -> "%s"
            else -> "%p"
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
