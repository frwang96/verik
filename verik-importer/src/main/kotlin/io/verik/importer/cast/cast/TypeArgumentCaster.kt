/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.ast.element.common.EContainerElement
import io.verik.importer.ast.element.descriptor.ETypeArgument
import io.verik.importer.cast.common.CastContext

/**
 * Caster for SystemVerilog type argument declarations.
 */
object TypeArgumentCaster {

    fun castTypeArgumentsFromListOfParameterAssignmentsOrdered(
        ctx: SystemVerilogParser.ListOfParameterAssignmentsOrderedContext,
        castContext: CastContext
    ): EContainerElement? {
        val typeArguments = ctx.orderedParameterAssignment().map { castContext.castTypeArgument(it) ?: return null }
        return EContainerElement(
            castContext.getLocation(ctx),
            typeArguments
        )
    }

    fun castTypeArgumentFromParamExpressionDataType(
        ctx: SystemVerilogParser.ParamExpressionDataTypeContext,
        castContext: CastContext
    ): ETypeArgument {
        val descriptor = castContext.castDescriptor(ctx.dataType())
        return ETypeArgument(
            descriptor.location,
            null,
            descriptor
        )
    }

    fun castTypeArgumentFromParamExpressionExpression(
        ctx: SystemVerilogParser.ParamExpressionExpressionContext,
        castContext: CastContext
    ): ETypeArgument {
        val descriptor = castContext.castDescriptor(ctx.minTypMaxExpression())
        return ETypeArgument(
            descriptor.location,
            null,
            descriptor
        )
    }
}
