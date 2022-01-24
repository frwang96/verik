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
import io.verik.importer.ast.sv.element.common.SvContainerElement
import io.verik.importer.ast.sv.element.declaration.SvProperty
import io.verik.importer.cast.common.CastContext
import io.verik.importer.cast.common.SignatureBuilder
import io.verik.importer.common.ElementCopier
import io.verik.importer.common.Type

object PropertyCaster {

    fun castPropertiesFromDataDeclarationData(
        ctx: SystemVerilogParser.DataDeclarationDataContext,
        castContext: CastContext
    ): SvContainerElement? {
        val isMutable = ctx.CONST() == null
        val descriptor = castContext.castDescriptor(ctx.dataTypeOrImplicit()) ?: return null
        val identifiers = ctx.listOfVariableDeclAssignments()
            .variableDeclAssignment()
            .filterIsInstance<SystemVerilogParser.VariableDeclAssignmentVariableContext>()
            .map { it.variableIdentifier() }
        val properties = identifiers.map {
            val location = castContext.getLocation(it)
            val name = it.text
            val signature = SignatureBuilder.buildSignature(ctx, name)
            SvProperty(
                location,
                name,
                signature,
                Type.unresolved(),
                ElementCopier.deepCopy(descriptor),
                isMutable
            )
        }
        return SvContainerElement(castContext.getLocation(ctx), properties)
    }

    fun castPropertiesFromStructUnionMember(
        ctx: SystemVerilogParser.StructUnionMemberContext,
        castContext: CastContext
    ): SvContainerElement? {
        val descriptor = castContext.castDescriptor(ctx.dataTypeOrVoid()) ?: return null
        val identifiers = ctx.listOfVariableDeclAssignments()
            .variableDeclAssignment()
            .filterIsInstance<SystemVerilogParser.VariableDeclAssignmentVariableContext>()
            .map { it.variableIdentifier() }
        val properties = identifiers.map {
            val location = castContext.getLocation(it)
            val name = it.text
            SvProperty(
                location,
                name,
                null,
                Type.unresolved(),
                ElementCopier.deepCopy(descriptor),
                true
            )
        }
        return SvContainerElement(castContext.getLocation(ctx), properties)
    }
}
