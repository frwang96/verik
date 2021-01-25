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

package verikc.rs.resolve

import verikc.base.ast.ExpressionClass.VALUE
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.LiteralValue
import verikc.lang.LangSymbol.TYPE_BOOLEAN
import verikc.lang.LangSymbol.TYPE_INT
import verikc.rs.ast.RsExpressionLiteral

object RsResolverLiteral {

    fun resolve(expression: RsExpressionLiteral) {
        resolveAsBool(expression.string)?.let {
            expression.typeGenerified = TYPE_BOOLEAN.toTypeGenerified()
            expression.expressionClass = VALUE
            expression.value = it
            return
        }

        resolveAsBin(expression.string, expression.line)?.let {
            expression.typeGenerified = TYPE_INT.toTypeGenerified()
            expression.expressionClass = VALUE
            expression.value = it
            return
        }

        resolveAsHex(expression.string, expression.line)?.let {
            expression.typeGenerified = TYPE_INT.toTypeGenerified()
            expression.expressionClass = VALUE
            expression.value = it
            return
        }

        resolveAsInt(expression.string, expression.line).let {
            expression.typeGenerified = TYPE_INT.toTypeGenerified()
            expression.expressionClass = VALUE
            expression.value = it
        }
    }

    private fun resolveAsBool(string: String): LiteralValue? {
        return when (string) {
            "true" -> LiteralValue.encodeBoolean(true)
            "false" -> LiteralValue.encodeBoolean(false)
            else -> null
        }
    }

    private fun resolveAsBin(string: String, line: Line): LiteralValue? {
        return if (string.startsWith("0b") || string.startsWith("0B")) {
            val strippedString = string.substring(2).replace("_", "")
            val value = strippedString.toIntOrNull(2)
                ?: throw LineException("unable to parse binary literal $string", line)
            LiteralValue.encodeInt(value)
        } else null
    }

    private fun resolveAsHex(string: String, line: Line): LiteralValue? {
        return if (string.startsWith("0x") || string.startsWith("0X")) {
            val strippedString = string.substring(2).replace("_", "")
            val value = strippedString.toIntOrNull(16)
                ?: throw LineException("unable to parse hexadecimal literal $string", line)
            LiteralValue.encodeInt(value)
        } else null
    }

    private fun resolveAsInt(string: String, line: Line): LiteralValue {
        val strippedString = string.replace("_", "")
        val value = strippedString.toIntOrNull()
            ?: throw LineException("unable to parse integer literal $string", line)
        return LiteralValue.encodeInt(value)
    }
}
