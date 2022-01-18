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

    fun castTypeFromDataType(
        ctx: SystemVerilogParser.DataTypeContext,
        castContext: CastContext
    ): Type? {
        return when (ctx.packedDimension().size) {
            0 -> Core.C_Boolean.toType()
            1 -> castContext.getType(ctx.packedDimension(0))?.let { Core.C_Ubit.toType(it) }
            else -> null
        }
    }

    fun castTypeFromDataTypeOrImplicit(
        ctx: SystemVerilogParser.DataTypeOrImplicitContext,
        castContext: CastContext
    ): Type? {
        val type = when {
            ctx.dataType() != null -> castContext.getType(ctx.dataType()!!)
            else -> castContext.getType(ctx.implicitDataType()!!)
        }
        if (type == null)
            Messages.TYPE_CAST_ERROR.on(castContext.getLocation(ctx), ctx.text)
        return type
    }

    fun castTypeFromImplicitDataType(
        ctx: SystemVerilogParser.ImplicitDataTypeContext,
        castContext: CastContext
    ): Type? {
        return when (ctx.packedDimension().size) {
            0 -> Core.C_Boolean.toType()
            1 -> castContext.getType(ctx.packedDimension(0))?.let { Core.C_Ubit.toType(it) }
            else -> null
        }
    }

    fun castTypeFromPackedDimension(
        ctx: SystemVerilogParser.PackedDimensionContext,
        castContext: CastContext
    ): Type? {
        ctx.constantRange()?.let { return castContext.getType(it) }
        Messages.TYPE_CAST_ERROR.on(castContext.getLocation(ctx), ctx.text)
        return null
    }

    fun castTypeFromConstantRange(
        ctx: SystemVerilogParser.ConstantRangeContext,
        castContext: CastContext
    ): Type? {
        return castContext.getType(ctx.constantExpression(0))
    }

    fun castTypeFromConstantExpression(ctx: SystemVerilogParser.ConstantExpressionContext): Type? {
        val value = ctx.text.toIntOrNull()
            ?: return null
        return Cardinal.of(value + 1).toType()
    }
}
