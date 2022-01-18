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
import io.verik.importer.core.Core
import io.verik.importer.message.Messages

object TypeCaster {

    fun castTypeFromDataTypeOrImplicit(
        ctx: SystemVerilogParser.DataTypeOrImplicitContext,
        castContext: CastContext
    ): Type? {
        val type = when {
            ctx.dataType() != null -> castTypeFromDataType(ctx.dataType()!!)
            else -> castTypeFromImplicitDataType(ctx.implicitDataType()!!)
        }
        if (type == null)
            Messages.TYPE_CAST_ERROR.on(castContext.getLocation(ctx), ctx.text)
        return type
    }

    private fun castTypeFromDataType(ctx: SystemVerilogParser.DataTypeContext): Type? {
        return when (ctx.packedDimension().size) {
            0 -> Core.C_Boolean.toType()
            1 -> castTypeFromPackedDimension(ctx.packedDimension(0))?.let { Core.C_Ubit.toType(it) }
            else -> null
        }
    }

    private fun castTypeFromImplicitDataType(ctx: SystemVerilogParser.ImplicitDataTypeContext): Type? {
        return when (ctx.packedDimension().size) {
            0 -> Core.C_Boolean.toType()
            1 -> castTypeFromPackedDimension(ctx.packedDimension(0))?.let { Core.C_Ubit.toType(it) }
            else -> null
        }
    }

    private fun castTypeFromPackedDimension(ctx: SystemVerilogParser.PackedDimensionContext): Type? {
        return when {
            ctx.constantRange() != null -> castTypeFromConstantRange(ctx.constantRange())
            else -> castTypeFromConstantRange(ctx.constantRange())
        }
    }

    private fun castTypeFromConstantRange(ctx: SystemVerilogParser.ConstantRangeContext): Type? {
        return castTypeFromConstantExpression(ctx.constantExpression(0))
    }

    private fun castTypeFromConstantExpression(ctx: SystemVerilogParser.ConstantExpressionContext): Type? {
        val value = ctx.text.toIntOrNull()
            ?: return null
        return Cardinal.of(value + 1).toType()
    }
}
