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
import io.verik.importer.ast.element.EModule
import io.verik.importer.ast.element.EPort
import io.verik.importer.ast.property.PortReference
import io.verik.importer.ast.property.PortType

object ModuleCaster {

    fun castModuleFromModuleDeclaration(
        ctx: SystemVerilogParser.ModuleDeclarationContext,
        castContext: CastContext
    ): EModule? {
        return when {
            ctx.moduleAnsiHeader() != null -> castModuleFromModuleAnsiHeader(ctx.moduleAnsiHeader(), castContext)
            else -> castModuleFromModuleNonAnsiHeader(ctx.moduleNonAnsiHeader(), castContext)
        }
    }

    private fun castModuleFromModuleAnsiHeader(
        ctx: SystemVerilogParser.ModuleAnsiHeaderContext,
        castContext: CastContext
    ): EModule? {
        val identifier = ctx.identifier()
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val ports = ctx.listOfPortDeclarations()?.ansiPortDeclaration()?.map {
            castPortFromAnsiPortDeclaration(it, castContext) ?: return null
        } ?: listOf()
        val portReferences = ports.map { PortReference(it, it.name) }
        return EModule(location, name, ports, portReferences)
    }

    private fun castModuleFromModuleNonAnsiHeader(
        ctx: SystemVerilogParser.ModuleNonAnsiHeaderContext,
        castContext: CastContext
    ): EModule {
        val identifier = ctx.identifier()
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        return EModule(location, name, listOf(), listOf())
    }

    private fun castPortFromAnsiPortDeclaration(
        ctx: SystemVerilogParser.AnsiPortDeclarationContext,
        castContext: CastContext
    ): EPort? {
        val identifier = ctx.identifier()
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val type = TypeCaster.castType(ctx.netPortHeader().netPortType().dataTypeOrImplicit(), castContext)
            ?: return null
        val portType = castPortTypeFromNetPortHeader(ctx.netPortHeader())
        return EPort(location, name, type, portType)
    }

    private fun castPortTypeFromNetPortHeader(ctx: SystemVerilogParser.NetPortHeaderContext): PortType {
        return when {
            ctx.portDirection()?.INPUT() != null -> PortType.INPUT
            ctx.portDirection()?.OUTPUT() != null -> PortType.OUTPUT
            else -> PortType.INPUT
        }
    }
}
