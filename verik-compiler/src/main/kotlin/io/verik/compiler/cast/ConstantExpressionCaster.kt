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

package io.verik.compiler.cast

import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.location
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtElement

object ConstantExpressionCaster {

    fun castConstantExpression(expression: KtConstantExpression, castContext: CastContext): EConstantExpression {
        val location = expression.location()
        val type = castContext.castType(expression)
        val value = castValue(expression.text, type, expression)
        return EConstantExpression(location, type, value)
    }

    private fun castValue(value: String, type: Type, element: KtElement): String {
        return when (type) {
            Core.Kt.BOOLEAN.toType() -> castBoolean(value)
            Core.Kt.INT.toType() -> castInteger(value)
            else -> {
                Messages.INTERNAL_ERROR.on(element, "Constant expression type not recognized: $type")
                ""
            }
        }
    }

    private fun castBoolean(value: String): String {
        return when (value.toBoolean()) {
            true -> "1'b1"
            false -> "1'b0"
        }
    }

    private fun castInteger(value: String): String {
        val valueWithoutSeparators = value.replace("_", "")
        val integerValue = when {
            value.startsWith("0x") || value.startsWith("0X") ->
                valueWithoutSeparators.substring(2).toInt(16)
            value.startsWith("0b") || value.startsWith("0B") ->
                valueWithoutSeparators.substring(2).toInt(2)
            else -> valueWithoutSeparators.toInt()
        }
        return integerValue.toString()
    }
}
