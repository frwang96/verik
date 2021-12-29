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

import io.verik.importer.antlr.parse.SystemVerilogParser
import io.verik.importer.ast.element.EModule
import io.verik.importer.ast.element.EPort
import io.verik.importer.ast.property.PortReference
import io.verik.importer.ast.property.PortType
import io.verik.importer.common.NullDeclaration

object ModuleCaster {

    fun castModule(ctx: SystemVerilogParser.ModuleDeclarationContext, castContext: CastContext): EModule? {
        return when {
            ctx.moduleAnsiHeader() != null -> castModuleWithModuleAnsiHeader(ctx, castContext)
            else -> castModuleWithModuleNonAnsiHeader(ctx, castContext)
        }
    }

    private fun castModuleWithModuleAnsiHeader(
        ctx: SystemVerilogParser.ModuleDeclarationContext,
        castContext: CastContext
    ): EModule? {
        val moduleAnsiHeader = ctx.moduleAnsiHeader()
        val identifier = moduleAnsiHeader.identifier()
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val ports = moduleAnsiHeader.listOfPortDeclarations()?.ansiPortDeclaration()?.map {
            castPort(it, castContext) ?: return null
        } ?: listOf()
        val portReferences = ports.map { PortReference(it, it.name) }
        return EModule(location, name, ports, portReferences)
    }

    private fun castModuleWithModuleNonAnsiHeader(
        ctx: SystemVerilogParser.ModuleDeclarationContext,
        castContext: CastContext
    ): EModule {
        val moduleNonAnsiHeader = ctx.moduleNonAnsiHeader()
        val identifier = moduleNonAnsiHeader.identifier()
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val ports = ctx.moduleItem().mapNotNull {
            if (it.portDeclaration() != null) {
                castPort(it.portDeclaration(), castContext)
            } else null
        }
        val portReferences = moduleNonAnsiHeader.listOfPorts().port().map { castPortReference(it) }
        return EModule(location, name, ports, portReferences)
    }

    private fun castPort(
        ctx: SystemVerilogParser.AnsiPortDeclarationContext,
        castContext: CastContext
    ): EPort? {
        val identifier = ctx.identifier()
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val type = TypeCaster.castType(ctx.netPortHeader().netPortType().dataTypeOrImplicit(), castContext)
            ?: return null
        val portType = castPortType(ctx.netPortHeader())
        return EPort(location, name, type, portType)
    }

    private fun castPort(
        ctx: SystemVerilogParser.PortDeclarationContext,
        castContext: CastContext
    ): EPort? {
        return when {
            ctx.inputDeclaration() != null -> castPort(ctx.inputDeclaration(), castContext)
            else -> castPort(ctx.outputDeclaration(), castContext)
        }
    }

    private fun castPort(
        ctx: SystemVerilogParser.InputDeclarationContext,
        castContext: CastContext
    ): EPort? {
        val identifier = ctx.identifier()
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val type = TypeCaster.castType(ctx.netPortType().dataTypeOrImplicit(), castContext)
            ?: return null
        return EPort(location, name, type, PortType.INPUT)
    }

    private fun castPort(
        ctx: SystemVerilogParser.OutputDeclarationContext,
        castContext: CastContext
    ): EPort? {
        val identifier = ctx.identifier()
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val type = TypeCaster.castType(ctx.netPortType().dataTypeOrImplicit(), castContext)
            ?: return null
        return EPort(location, name, type, PortType.OUTPUT)
    }

    private fun castPortReference(ctx: SystemVerilogParser.PortContext): PortReference {
        val identifier = ctx.portExpression().portReference().identifier()
        return PortReference(NullDeclaration, identifier.text)
    }

    private fun castPortType(ctx: SystemVerilogParser.NetPortHeaderContext): PortType {
        return when {
            ctx.portDirection()?.INPUT() != null -> PortType.INPUT
            ctx.portDirection()?.OUTPUT() != null -> PortType.OUTPUT
            else -> PortType.INPUT
        }
    }
}
