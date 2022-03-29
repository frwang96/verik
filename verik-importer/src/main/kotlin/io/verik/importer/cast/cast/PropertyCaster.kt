/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.ast.element.common.EContainerElement
import io.verik.importer.ast.element.declaration.EProperty
import io.verik.importer.cast.common.CastContext
import io.verik.importer.cast.common.SignatureBuilder
import io.verik.importer.common.ElementCopier

/**
 * Caster for SystemVerilog property declarations.
 */
object PropertyCaster {

    fun castPropertiesFromClassProperty(
        ctx: SystemVerilogParser.ClassPropertyContext,
        castContext: CastContext
    ): EContainerElement {
        val declarations = castContext.castDeclarations(ctx.dataDeclaration())
        val isStatic = ctx.propertyQualifier().any { it.text == "static" }
        declarations.forEach {
            it.signature = SignatureBuilder.buildSignature(ctx, it.name)
            if (it is EProperty) it.isStatic = isStatic
        }
        return EContainerElement(castContext.getLocation(ctx), declarations)
    }

    fun castPropertiesFromDataDeclarationData(
        ctx: SystemVerilogParser.DataDeclarationDataContext,
        castContext: CastContext
    ): EContainerElement {
        val isMutable = ctx.CONST() == null
        val baseDescriptor = castContext.castDescriptor(ctx.dataTypeOrImplicit())
        val variableDeclAssignmentVariables = ctx.listOfVariableDeclAssignments()
            .variableDeclAssignment()
            .filterIsInstance<SystemVerilogParser.VariableDeclAssignmentVariableContext>()
        val properties = variableDeclAssignmentVariables.map { variableDeclAssignmentVariable ->
            val identifier = variableDeclAssignmentVariable.variableIdentifier()
            val location = castContext.getLocation(identifier)
            val name = identifier.text
            val signature = SignatureBuilder.buildSignature(ctx, name)
            val baseDescriptorCopy = ElementCopier.deepCopy(baseDescriptor)
            val descriptors = variableDeclAssignmentVariable.variableDimension()
                .map { castContext.castDescriptor(it) }
            val descriptor = descriptors.fold(baseDescriptorCopy) { accumulatedDescriptor, descriptor ->
                accumulatedDescriptor.wrap(descriptor)
            }
            EProperty(
                location,
                name,
                signature,
                descriptor,
                false,
                isMutable
            )
        }
        return EContainerElement(castContext.getLocation(ctx), properties)
    }
}
