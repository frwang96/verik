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
import io.verik.importer.ast.sv.element.declaration.SvDeclaration
import io.verik.importer.ast.sv.element.declaration.SvModule
import io.verik.importer.ast.sv.element.declaration.SvPort
import io.verik.importer.message.Messages

object ModuleCaster {

    fun castModuleFromModuleDeclarationNonAnsi(
        ctx: SystemVerilogParser.ModuleDeclarationNonAnsiContext,
        castContext: CastContext
    ): SvModule? {
        val identifier = ctx.moduleNonAnsiHeader().moduleIdentifier()
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val declarations = ArrayList<SvDeclaration>()
        val ports = ArrayList<SvPort>()
        ctx.moduleItem().forEach {
            if (it is SystemVerilogParser.ModuleItemPortDeclarationContext) {
                val port = castContext.getPort(it)
                if (port == null) {
                    Messages.UNABLE_TO_CAST.on(castContext.getLocation(it), "port")
                    return null
                }
                ports.add(port)
            } else {
                val declaration = castContext.getDeclaration(it)
                if (declaration != null)
                    declarations.add(declaration)
            }
        }
        return SvModule(location, name, declarations, ports)
    }

    fun castModuleFromModuleDeclarationAnsi(
        ctx: SystemVerilogParser.ModuleDeclarationAnsiContext,
        castContext: CastContext
    ): SvModule? {
        val moduleAnsiHeader = ctx.moduleAnsiHeader()
        val identifier = moduleAnsiHeader.moduleIdentifier()
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val declarations = ctx.nonPortModuleItem().mapNotNull { castContext.getDeclaration(it) }
        val ports = moduleAnsiHeader.listOfPortDeclarations()?.ansiPortDeclaration()?.map {
            castContext.getPort(it) ?: return null
        } ?: listOf()
        return SvModule(
            location,
            name,
            ArrayList(declarations),
            ArrayList(ports)
        )
    }
}
