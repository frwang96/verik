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
import io.verik.importer.ast.sv.element.declaration.SvProperty
import io.verik.importer.common.Type

object PropertyCaster {

    fun castPropertyFromDataDeclarationData(
        ctx: SystemVerilogParser.DataDeclarationDataContext,
        castContext: CastContext
    ): SvProperty? {
        val variableDeclAssignment = ctx.listOfVariableDeclAssignments().variableDeclAssignment(0)
        val variableIdentifier = (variableDeclAssignment as SystemVerilogParser.VariableDeclAssignmentVariableContext)
            .variableIdentifier()
        val location = castContext.getLocation(variableIdentifier)
        val name = variableIdentifier.text
        val descriptor = castContext.getDescriptor(ctx.dataTypeOrImplicit()) ?: return null
        return SvProperty(
            location,
            name,
            Type.unresolved(),
            descriptor
        )
    }
}
