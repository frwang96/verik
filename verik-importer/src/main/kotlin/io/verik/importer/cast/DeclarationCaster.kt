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
import io.verik.importer.ast.element.EProperty

object DeclarationCaster {

    fun castModule(ctx: SystemVerilogParser.ModuleDeclarationContext, castContext: CastContext): EModule {
        val moduleAnsiHeader = ctx.moduleAnsiHeader()
        val moduleNonAnsiHeader = ctx.moduleNonAnsiHeader()
        val identifier = moduleAnsiHeader?.identifier()
            ?: moduleNonAnsiHeader.identifier()
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        return EModule(location, name)
    }

    fun castProperty(ctx: SystemVerilogParser.DataDeclarationContext, castContext: CastContext): EProperty? {
        val identifier = ctx.listOfVariableDeclAssignments().variableDeclAssignment(0).identifier()
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val type = TypeCaster.castType(ctx.dataTypeOrImplicit(), castContext)
            ?: return null
        return EProperty(location, name, type)
    }
}
