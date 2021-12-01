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

package io.verik.importer.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.ast.property.Type
import io.verik.importer.core.Cardinal

object CardinalTypeCaster {

    fun castCardinalType(ctx: SystemVerilogParser.PackedDimensionContext): Type? {
        return when {
            ctx.constantRange() != null -> castCardinalTypeFromConstantRange(ctx.constantRange())
            else -> castCardinalTypeFromConstantRange(ctx.constantRange())
        }
    }

    private fun castCardinalTypeFromConstantRange(ctx: SystemVerilogParser.ConstantRangeContext): Type? {
        return castCardinalTypeFromConstantExpression(ctx.constantExpression(0))
    }

    private fun castCardinalTypeFromConstantExpression(ctx: SystemVerilogParser.ConstantExpressionContext): Type? {
        val value = ctx.text.toIntOrNull()
            ?: return null
        return Cardinal.of(value + 1).toType()
    }
}
