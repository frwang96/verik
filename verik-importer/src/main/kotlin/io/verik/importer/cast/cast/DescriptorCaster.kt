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
import io.verik.importer.ast.common.Type
import io.verik.importer.ast.element.descriptor.EArrayDimensionDescriptor
import io.verik.importer.ast.element.descriptor.EBitDescriptor
import io.verik.importer.ast.element.descriptor.EDescriptor
import io.verik.importer.ast.element.descriptor.EIndexDimensionDescriptor
import io.verik.importer.ast.element.descriptor.ELiteralDescriptor
import io.verik.importer.ast.element.descriptor.ERangeDimensionDescriptor
import io.verik.importer.ast.element.descriptor.EReferenceDescriptor
import io.verik.importer.ast.element.descriptor.ESimpleDescriptor
import io.verik.importer.cast.common.CastContext
import io.verik.importer.core.Cardinal
import io.verik.importer.core.Core
import io.verik.importer.message.SourceLocation

object DescriptorCaster {

    fun castDescriptorFromDataTypeVector(
        ctx: SystemVerilogParser.DataTypeVectorContext,
        castContext: CastContext
    ): EDescriptor? {
        val location = castContext.getLocation(ctx)
        val isSigned = castIsSignedFromSigning(ctx.signing()) ?: false
        return castDescriptorFromPackedDimensions(
            location,
            ctx.packedDimension(),
            isSigned,
            castContext
        )
    }

    fun castDescriptorFromDataTypeInteger(
        ctx: SystemVerilogParser.DataTypeIntegerContext,
        castContext: CastContext
    ): EDescriptor? {
        val location = castContext.getLocation(ctx)
        val isSigned = castIsSignedFromSigning(ctx.signing()) ?: true
        val integerAtomType = ctx.integerAtomType()
        return when {
            integerAtomType.INT() != null || integerAtomType.INTEGER() != null -> {
                if (isSigned) {
                    ESimpleDescriptor(location, Core.C_Int.toType())
                } else {
                    ESimpleDescriptor(location, Core.C_Ubit.toType(Cardinal.of(32).toType()))
                }
            }
            integerAtomType.TIME() != null ->
                ESimpleDescriptor(location, Core.C_Time.toType())
            else -> null
        }
    }

    fun castDescriptorFromDataTypeString(
        ctx: SystemVerilogParser.DataTypeStringContext,
        castContext: CastContext
    ): EDescriptor {
        val location = castContext.getLocation(ctx)
        return ESimpleDescriptor(location, Core.C_String.toType())
    }

    fun castDescriptorFromDataTypeTypeIdentifier(
        ctx: SystemVerilogParser.DataTypeTypeIdentifierContext,
        castContext: CastContext
    ): EDescriptor {
        val location = castContext.getLocation(ctx)
        val name = ctx.typeIdentifier().text
        return EReferenceDescriptor(location, Type.unresolved(), name, Core.C_Nothing, listOf())
    }

    fun castDescriptorFromImplicitDataType(
        ctx: SystemVerilogParser.ImplicitDataTypeContext,
        castContext: CastContext
    ): EDescriptor? {
        val location = castContext.getLocation(ctx)
        val isSigned = castIsSignedFromSigning(ctx.signing()) ?: false
        return castDescriptorFromPackedDimensions(
            location,
            ctx.packedDimension(),
            isSigned,
            castContext
        )
    }

    fun castDescriptorFromClassType(
        ctx: SystemVerilogParser.ClassTypeContext,
        castContext: CastContext
    ): EDescriptor {
        val location = castContext.getLocation(ctx)
        val identifier = ctx.psClassIdentifier().classIdentifier()
        val name = identifier.text
        val typeArguments = if (ctx.parameterValueAssignment().isNotEmpty()) {
            castContext.castTypeArguments(ctx.parameterValueAssignment(0))
        } else listOf()
        return EReferenceDescriptor(location, Type.unresolved(), name, Core.C_Nothing, typeArguments)
    }

    fun castDescriptorFromDataTypeOrVoid(
        ctx: SystemVerilogParser.DataTypeOrVoidContext,
        castContext: CastContext
    ): EDescriptor {
        val dataType = ctx.dataType()
        return if (dataType != null) {
            castContext.castDescriptor(dataType)
        } else {
            val location = castContext.getLocation(ctx)
            ESimpleDescriptor(location, Core.C_Unit.toType())
        }
    }

    fun castDescriptorFromUnpackedDimensionRange(
        ctx: SystemVerilogParser.UnpackedDimensionRangeContext,
        castContext: CastContext
    ): EDescriptor {
        val location = castContext.getLocation(ctx)
        val left = castContext.castDescriptor(ctx.constantRange().constantExpression(0))
        val right = castContext.castDescriptor(ctx.constantRange().constantExpression(1))
        return ERangeDimensionDescriptor(
            location,
            Type.unresolved(),
            ESimpleDescriptor(location, Core.C_Boolean.toType()),
            left,
            right,
            false
        )
    }

    fun castDescriptorFromUnpackedDimensionExpression(
        ctx: SystemVerilogParser.UnpackedDimensionExpressionContext,
        castContext: CastContext
    ): EDescriptor {
        val location = castContext.getLocation(ctx)
        val index = castContext.castDescriptor(ctx.constantExpression())
        return EIndexDimensionDescriptor(
            location,
            Type.unresolved(),
            ESimpleDescriptor(location, Core.C_Boolean.toType()),
            index
        )
    }

    fun castDescriptorFromAssociativeDimension(
        ctx: SystemVerilogParser.AssociativeDimensionContext,
        castContext: CastContext
    ): EDescriptor {
        val location = castContext.getLocation(ctx)
        val dataType = ctx.dataType()
        return if (dataType != null) {
            val index = castContext.castDescriptor(dataType)
            EIndexDimensionDescriptor(
                location,
                Type.unresolved(),
                ESimpleDescriptor(location, Core.C_Boolean.toType()),
                index
            )
        } else {
            EIndexDimensionDescriptor(
                location,
                Type.unresolved(),
                ESimpleDescriptor(location, Core.C_Boolean.toType()),
                ESimpleDescriptor(location, Core.C_Int.toType())
            )
        }
    }

    fun castDescriptorFromQueueDimension(
        ctx: SystemVerilogParser.QueueDimensionContext,
        castContext: CastContext
    ): EDescriptor {
        val location = castContext.getLocation(ctx)
        return EArrayDimensionDescriptor(
            location,
            Type.unresolved(),
            ESimpleDescriptor(location, Core.C_Boolean.toType()),
            true
        )
    }

    fun castDescriptorFromUnsizedDimension(
        ctx: SystemVerilogParser.UnsizedDimensionContext,
        castContext: CastContext
    ): EDescriptor {
        val location = castContext.getLocation(ctx)
        return EArrayDimensionDescriptor(
            location,
            Type.unresolved(),
            ESimpleDescriptor(location, Core.C_Boolean.toType()),
            false
        )
    }

    fun castDescriptorFromConstantPrimaryParameter(
        ctx: SystemVerilogParser.ConstantPrimaryParameterContext,
        castContext: CastContext
    ): EDescriptor {
        val identifier = ctx.psParameterIdentifier().parameterIdentifier()
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        return EReferenceDescriptor(location, Type.unresolved(), name, Core.C_Nothing, listOf())
    }

    fun castDescriptorFromPrimaryLiteral(
        ctx: SystemVerilogParser.PrimaryLiteralContext,
        castContext: CastContext
    ): EDescriptor {
        val location = castContext.getLocation(ctx)
        return ELiteralDescriptor(location, Type.unresolved(), ctx.text)
    }

    fun castDescriptorFromPrimaryHierarchical(
        ctx: SystemVerilogParser.PrimaryHierarchicalContext,
        castContext: CastContext
    ): EDescriptor {
        val identifier = ctx.hierarchicalIdentifier()
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        return EReferenceDescriptor(location, Type.unresolved(), name, Core.C_Nothing, listOf())
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

    private fun castDescriptorFromPackedDimensions(
        location: SourceLocation,
        ctxs: List<SystemVerilogParser.PackedDimensionContext>,
        isSigned: Boolean,
        castContext: CastContext
    ): EDescriptor? {
        if (ctxs.isEmpty()) {
            return ESimpleDescriptor(location, Core.C_Boolean.toType())
        }
        val packedDimensionRanges = ctxs.map {
            if (it is SystemVerilogParser.PackedDimensionRangeContext) it
            else return null
        }
        val baseDescriptor: EDescriptor = EBitDescriptor(
            location,
            Type.unresolved(),
            castContext.castDescriptor(packedDimensionRanges[0].constantRange().constantExpression()[0]),
            castContext.castDescriptor(packedDimensionRanges[0].constantRange().constantExpression()[1]),
            isSigned
        )
        val rangeDimensionDescriptors = packedDimensionRanges.drop(1).map {
            val packedDescriptorLocation = castContext.getLocation(it)
            ERangeDimensionDescriptor(
                packedDescriptorLocation,
                Type.unresolved(),
                ESimpleDescriptor(packedDescriptorLocation, Core.C_Boolean.toType()),
                castContext.castDescriptor(it.constantRange().constantExpression(0)),
                castContext.castDescriptor(it.constantRange().constantExpression(1)),
                true
            )
        }
        return rangeDimensionDescriptors.fold(baseDescriptor) { accumulatedDescriptor, descriptor ->
            accumulatedDescriptor.wrap(descriptor)
        }
    }
}
