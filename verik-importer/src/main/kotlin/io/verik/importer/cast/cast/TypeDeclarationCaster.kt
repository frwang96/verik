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
import io.verik.importer.ast.sv.element.declaration.SvEnum
import io.verik.importer.ast.sv.element.declaration.SvEnumEntry
import io.verik.importer.ast.sv.element.declaration.SvStruct
import io.verik.importer.ast.sv.element.declaration.SvStructEntry
import io.verik.importer.ast.sv.element.declaration.SvTypeAlias
import io.verik.importer.ast.sv.element.declaration.SvTypeDeclaration
import io.verik.importer.cast.common.CastContext
import io.verik.importer.cast.common.SignatureBuilder
import io.verik.importer.common.ElementCopier
import io.verik.importer.message.SourceLocation

object TypeDeclarationCaster {

    fun castTypeDeclarationFromTypeDeclarationData(
        ctx: SystemVerilogParser.TypeDeclarationDataContext,
        castContext: CastContext
    ): SvTypeDeclaration? {
        val identifier = ctx.typeIdentifier()
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val signature = SignatureBuilder.buildSignature(ctx, name)
        return when (val dataType = ctx.dataType()) {
            is SystemVerilogParser.DataTypeEnumContext ->
                castEnumFromDataTypeEnum(location, name, signature, dataType, castContext)
            is SystemVerilogParser.DataTypeStructContext ->
                castStructFromDataTypeStruct(location, name, signature, dataType, castContext)
            else -> {
                val descriptor = castContext.castDescriptor(dataType) ?: return null
                SvTypeAlias(location, name, signature, descriptor)
            }
        }
    }

    private fun castEnumFromDataTypeEnum(
        location: SourceLocation,
        name: String,
        signature: String,
        dataTypeEnum: SystemVerilogParser.DataTypeEnumContext,
        castContext: CastContext
    ): SvEnum {
        val entries = dataTypeEnum.enumNameDeclaration().map {
            castEnumEntryFromEnumNameDeclaration(it, castContext)
        }
        return SvEnum(location, name, signature, entries)
    }

    private fun castEnumEntryFromEnumNameDeclaration(
        ctx: SystemVerilogParser.EnumNameDeclarationContext,
        castContext: CastContext
    ): SvEnumEntry {
        val identifier = ctx.enumIdentifier()
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        return SvEnumEntry(location, name)
    }

    private fun castStructFromDataTypeStruct(
        location: SourceLocation,
        name: String,
        signature: String,
        dataTypeStruct: SystemVerilogParser.DataTypeStructContext,
        castContext: CastContext
    ): SvStruct {
        val entries = dataTypeStruct.structUnionMember().flatMap {
            castStructEntriesFromStructUnionMember(it, castContext)
        }
        return SvStruct(
            location,
            name,
            signature,
            entries
        )
    }

    private fun castStructEntriesFromStructUnionMember(
        ctx: SystemVerilogParser.StructUnionMemberContext,
        castContext: CastContext
    ): List<SvStructEntry> {
        val descriptor = castContext.castDescriptor(ctx.dataTypeOrVoid()) ?: return listOf()
        val identifiers = ctx.listOfVariableDeclAssignments()
            .variableDeclAssignment()
            .filterIsInstance<SystemVerilogParser.VariableDeclAssignmentVariableContext>()
            .map { it.variableIdentifier() }
        return identifiers.map {
            val location = castContext.getLocation(it)
            val name = it.text
            SvStructEntry(location, name, ElementCopier.deepCopy(descriptor))
        }
    }
}
