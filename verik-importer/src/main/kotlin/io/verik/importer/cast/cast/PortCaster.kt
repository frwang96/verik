/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.ast.element.declaration.EPort
import io.verik.importer.ast.property.PortType
import io.verik.importer.cast.common.CastContext

object PortCaster {

    fun castPortFromAnsiPortDeclaration(
        ctx: SystemVerilogParser.AnsiPortDeclarationContext,
        castContext: CastContext
    ): EPort {
        val identifier = ctx.identifier()
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val descriptor = castContext.castDescriptor(ctx.netPortHeader())
        val portType = castPortType(ctx.netPortHeader())
        return EPort(
            location,
            name,
            descriptor,
            portType
        )
    }

    fun castPortFromInputDeclarationNet(
        ctx: SystemVerilogParser.InputDeclarationNetContext,
        castContext: CastContext
    ): EPort {
        val identifier = ctx.listOfPortIdentifiers().portIdentifier()[0]
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val descriptor = castContext.castDescriptor(ctx.netPortType())
        return EPort(
            location,
            name,
            descriptor,
            PortType.INPUT
        )
    }

    fun castPortFromOutputDeclarationNet(
        ctx: SystemVerilogParser.OutputDeclarationNetContext,
        castContext: CastContext
    ): EPort {
        val identifier = ctx.listOfPortIdentifiers().portIdentifier()[0]
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val descriptor = castContext.castDescriptor(ctx.netPortType())
        return EPort(
            location,
            name,
            descriptor,
            PortType.OUTPUT
        )
    }

    private fun castPortType(ctx: SystemVerilogParser.NetPortHeaderContext): PortType {
        return when {
            ctx.portDirection()?.INPUT() != null -> PortType.INPUT
            ctx.portDirection()?.OUTPUT() != null -> PortType.OUTPUT
            else -> PortType.INPUT
        }
    }
}
