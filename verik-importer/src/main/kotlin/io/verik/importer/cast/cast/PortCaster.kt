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
import io.verik.importer.ast.element.declaration.EPort
import io.verik.importer.ast.property.PortType
import io.verik.importer.cast.common.CastContext

object PortCaster {

    fun castPortFromAnsiPortDeclaration(
        ctx: SystemVerilogParser.AnsiPortDeclarationContext,
        castContext: CastContext
    ): EPort? {
        val identifier = ctx.identifier()
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val descriptor = castContext.castDescriptor(ctx.netPortHeader()) ?: return null
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
    ): EPort? {
        val identifier = ctx.listOfPortIdentifiers().portIdentifier()[0]
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val descriptor = castContext.castDescriptor(ctx.netPortType()) ?: return null
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
    ): EPort? {
        val identifier = ctx.listOfPortIdentifiers().portIdentifier()[0]
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val descriptor = castContext.castDescriptor(ctx.netPortType()) ?: return null
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
