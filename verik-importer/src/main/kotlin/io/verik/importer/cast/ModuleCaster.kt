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

object ModuleCaster {

    fun castModuleFromModuleDeclarationNonAnsi(
        ctx: SystemVerilogParser.ModuleDeclarationNonAnsiContext,
        castContext: CastContext
    ): EModule? {
        val moduleNonAnsiHeader = ctx.moduleNonAnsiHeader()
        val identifier = moduleNonAnsiHeader.identifier()
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val ports = ctx.moduleItem().mapNotNull {
            if (it.portDeclaration() != null) {
                castContext.getPort(it.portDeclaration()) ?: return null
            } else null
        }
        return EModule(location, name, ports)
    }

    fun castModuleFromModuleDeclarationAnsi(
        ctx: SystemVerilogParser.ModuleDeclarationAnsiContext,
        castContext: CastContext
    ): EModule? {
        val moduleAnsiHeader = ctx.moduleAnsiHeader()
        val identifier = moduleAnsiHeader.identifier()
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val ports = moduleAnsiHeader.listOfPortDeclarations()?.ansiPortDeclaration()?.map {
            castContext.getPort(it) ?: return null
        } ?: listOf()
        return EModule(location, name, ports)
    }
}
