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

package io.verik.importer.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.ast.element.EPort
import io.verik.importer.ast.property.PortType

object PortCaster {

    fun castPortFromPortDeclaration(
        ctx: SystemVerilogParser.PortDeclarationContext,
        castContext: CastContext
    ): EPort? {
        ctx.inputDeclaration()?.let { return castContext.getPort(it) }
        ctx.outputDeclaration()?.let { return castContext.getPort(it) }
        return null
    }

    fun castPortFromAnsiPortDeclaration(
        ctx: SystemVerilogParser.AnsiPortDeclarationContext,
        castContext: CastContext
    ): EPort? {
        val identifier = ctx.identifier()
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val type = castContext.getType(ctx.netPortHeader().netPortType().dataTypeOrImplicit()) ?: return null
        val portType = castPortType(ctx.netPortHeader())
        return EPort(location, name, type, portType)
    }

    private fun castPortType(ctx: SystemVerilogParser.NetPortHeaderContext): PortType {
        return when {
            ctx.portDirection()?.INPUT() != null -> PortType.INPUT
            ctx.portDirection()?.OUTPUT() != null -> PortType.OUTPUT
            else -> PortType.INPUT
        }
    }
}
