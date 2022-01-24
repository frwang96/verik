/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.importer.cast.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.ast.sv.element.descriptor.SvBitDescriptor
import io.verik.importer.ast.sv.element.descriptor.SvDescriptor
import io.verik.importer.ast.sv.element.descriptor.SvPackedDescriptor
import io.verik.importer.ast.sv.element.descriptor.SvSimpleDescriptor
import io.verik.importer.cast.common.CastContext
import io.verik.importer.common.Type
import io.verik.importer.core.Core
import io.verik.importer.message.SourceLocation

object DescriptorCaster {

    fun castDescriptorFromDataTypeVector(
        ctx: SystemVerilogParser.DataTypeVectorContext,
        castContext: CastContext
    ): SvDescriptor? {
        val location = castContext.getLocation(ctx)
        val isSigned = castIsSignedFromSigning(ctx.signing()) ?: false
        return castDescriptorFromPackedDimension(
            location,
            ctx.packedDimension(),
            isSigned,
            castContext
        )
    }

    fun castDescriptorFromDataTypeInteger(
        ctx: SystemVerilogParser.DataTypeIntegerContext,
        castContext: CastContext
    ): SvDescriptor? {
        val location = castContext.getLocation(ctx)
        val isSigned = castIsSignedFromSigning(ctx.signing()) ?: true
        val integerAtomType = ctx.integerAtomType()
        return when {
            isSigned && (integerAtomType.INT() != null || integerAtomType.INTEGER() != null) ->
                SvSimpleDescriptor(location, Core.C_Int.toType())
            integerAtomType.TIME() != null ->
                SvSimpleDescriptor(location, Core.C_Time.toType())
            else -> null
        }
    }

    fun castDescriptorFromImplicitDataType(
        ctx: SystemVerilogParser.ImplicitDataTypeContext,
        castContext: CastContext
    ): SvDescriptor? {
        val location = castContext.getLocation(ctx)
        val isSigned = castIsSignedFromSigning(ctx.signing()) ?: false
        return castDescriptorFromPackedDimension(
            location,
            ctx.packedDimension(),
            isSigned,
            castContext
        )
    }

    fun castDescriptorFromDataTypeOrVoid(
        ctx: SystemVerilogParser.DataTypeOrVoidContext,
        castContext: CastContext
    ): SvDescriptor? {
        val dataType = ctx.dataType()
        return if (dataType != null) {
            castContext.castDescriptor(dataType)
        } else {
            val location = castContext.getLocation(ctx)
            SvSimpleDescriptor(location, Core.C_Unit.toType())
        }
    }

    private fun castIsSignedFromSigning(
        ctx: SystemVerilogParser.SigningContext?
    ): Boolean? {
        return when {
            ctx == null -> null
            ctx.SIGNED() != null -> true
            else -> false
        }
    }

    private fun castDescriptorFromPackedDimension(
        location: SourceLocation,
        ctxs: List<SystemVerilogParser.PackedDimensionContext>,
        isSigned: Boolean,
        castContext: CastContext
    ): SvDescriptor? {
        if (ctxs.isEmpty()) {
            return SvSimpleDescriptor(location, Core.C_Boolean.toType())
        }
        val packedDimensionRanges = ctxs.map {
            if (it is SystemVerilogParser.PackedDimensionRangeContext) it
            else return null
        }
        val initialDescriptor: SvDescriptor = SvBitDescriptor(
            location,
            Type.unresolved(),
            castContext.castExpression(packedDimensionRanges[0].constantRange().constantExpression()[0]),
            castContext.castExpression(packedDimensionRanges[0].constantRange().constantExpression()[1]),
            isSigned
        )
        return packedDimensionRanges
            .drop(1)
            .fold(initialDescriptor) { descriptor, packedDimensionRange ->
                SvPackedDescriptor(
                    castContext.getLocation(packedDimensionRange),
                    Type.unresolved(),
                    descriptor,
                    castContext.castExpression(packedDimensionRange.constantRange().constantExpression(0)),
                    castContext.castExpression(packedDimensionRange.constantRange().constantExpression(1))
                )
            }
    }
}
