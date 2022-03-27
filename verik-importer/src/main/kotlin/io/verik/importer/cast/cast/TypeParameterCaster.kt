/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.ast.element.common.EContainerElement
import io.verik.importer.ast.element.declaration.ETypeParameter
import io.verik.importer.cast.common.CastContext

object TypeParameterCaster {

    fun castTypeParametersFromParameterPortListDeclaration(
        ctx: SystemVerilogParser.ParameterPortListDeclarationContext,
        castContext: CastContext
    ): EContainerElement {
        val typeParameters = ctx.parameterPortDeclaration().flatMap { castContext.castTypeParameters(it) }
        return EContainerElement(castContext.getLocation(ctx), typeParameters)
    }

    fun castTypeParameterFromParameterPortDeclarationDataType(
        ctx: SystemVerilogParser.ParameterPortDeclarationDataTypeContext,
        castContext: CastContext
    ): EContainerElement? {
        when (val dataType = ctx.dataType()) {
            is SystemVerilogParser.DataTypeVectorContext -> {
                if (dataType.packedDimension().size > 1) {
                    return null
                }
            }
            is SystemVerilogParser.DataTypeIntegerContext -> {}
            else -> return null
        }
        val paramAssignments = ctx.listOfParamAssignments().paramAssignment()
        val typeParameters = paramAssignments.mapNotNull { paramAssignment ->
            val identifier = paramAssignment.parameterIdentifier()
            val location = castContext.getLocation(identifier)
            val name = identifier.text
            val descriptor = paramAssignment.constantParamExpression()?.let { castContext.castDescriptor(it) }
            if (paramAssignment.unpackedDimension().isEmpty()) {
                ETypeParameter(location, name, descriptor, true)
            } else null
        }
        return EContainerElement(castContext.getLocation(ctx), typeParameters)
    }

    fun castTypeParameterFromParameterPortDeclarationType(
        ctx: SystemVerilogParser.ParameterPortDeclarationTypeContext,
        castContext: CastContext
    ): EContainerElement {
        val paramAssignments = ctx.listOfParamAssignments().paramAssignment()
        val typeParameters = paramAssignments.mapNotNull { paramAssignment ->
            val identifier = paramAssignment.parameterIdentifier()
            val location = castContext.getLocation(identifier)
            val name = identifier.text
            val descriptor = paramAssignment.constantParamExpression()?.let { castContext.castDescriptor(it) }
            if (paramAssignment.unpackedDimension().isEmpty()) {
                ETypeParameter(location, name, descriptor, false)
            } else null
        }
        return EContainerElement(castContext.getLocation(ctx), typeParameters)
    }
}
