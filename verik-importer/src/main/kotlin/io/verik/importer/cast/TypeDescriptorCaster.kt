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
import io.verik.importer.ast.sv.element.common.SvPackedTypeDescriptor
import io.verik.importer.ast.sv.element.common.SvSimpleTypeDescriptor
import io.verik.importer.ast.sv.element.common.SvTypeDescriptor
import io.verik.importer.core.Core

object TypeDescriptorCaster {

    fun castTypeDescriptorFromDataTypeVector(
        ctx: SystemVerilogParser.DataTypeVectorContext,
        castContext: CastContext
    ): SvTypeDescriptor? {
        var typeDescriptor = castContext.getTypeDescriptor(ctx.integerVectorType()) ?: return null
        ctx.packedDimension().forEach {
            if (it is SystemVerilogParser.PackedDimensionRangeContext) {
                val location = castContext.getLocation(it)
                val left = castContext.getExpression(it.constantRange().constantExpression(0))
                val right = castContext.getExpression(it.constantRange().constantExpression(1))
                typeDescriptor = SvPackedTypeDescriptor(location, Core.C_Nothing.toType(), typeDescriptor, left, right)
            }
        }
        return typeDescriptor
    }

    fun castTypeDescriptorFromImplicitDataType(
        ctx: SystemVerilogParser.ImplicitDataTypeContext,
        castContext: CastContext
    ): SvTypeDescriptor? {
        return when (ctx.packedDimension().size) {
            0 -> {
                val location = castContext.getLocation(ctx)
                SvSimpleTypeDescriptor(location, Core.C_Boolean.toType())
            }
            else -> null
        }
    }

    fun castTypeDescriptorFromIntegerVectorType(
        ctx: SystemVerilogParser.IntegerVectorTypeContext,
        castContext: CastContext
    ): SvTypeDescriptor {
        val location = castContext.getLocation(ctx)
        return SvSimpleTypeDescriptor(location, Core.C_Boolean.toType())
    }
}
