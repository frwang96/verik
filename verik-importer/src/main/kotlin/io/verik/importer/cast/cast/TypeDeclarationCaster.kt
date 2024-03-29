/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.ast.element.declaration.EEnum
import io.verik.importer.ast.element.declaration.EEnumEntry
import io.verik.importer.ast.element.declaration.EStruct
import io.verik.importer.ast.element.declaration.EStructEntry
import io.verik.importer.ast.element.declaration.ETypeAlias
import io.verik.importer.ast.element.declaration.ETypeDeclaration
import io.verik.importer.cast.common.CastContext
import io.verik.importer.cast.common.SignatureBuilder
import io.verik.importer.common.ElementCopier
import io.verik.importer.message.SourceLocation

/**
 * Caster for SystemVerilog type definition declarations.
 */
object TypeDeclarationCaster {

    fun castTypeDeclarationFromTypeDeclarationData(
        ctx: SystemVerilogParser.TypeDeclarationDataContext,
        castContext: CastContext
    ): ETypeDeclaration {
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
                val baseDescriptor = castContext.castDescriptor(dataType)
                val descriptors = ctx.variableDimension().map { castContext.castDescriptor(it) }
                val descriptor = descriptors.fold(baseDescriptor) { accumulatedDescriptor, descriptor ->
                    accumulatedDescriptor.wrap(descriptor)
                }
                ETypeAlias(location, name, signature, descriptor)
            }
        }
    }

    private fun castEnumFromDataTypeEnum(
        location: SourceLocation,
        name: String,
        signature: String,
        dataTypeEnum: SystemVerilogParser.DataTypeEnumContext,
        castContext: CastContext
    ): EEnum {
        val entries = dataTypeEnum.enumNameDeclaration().map {
            castEnumEntryFromEnumNameDeclaration(it, castContext)
        }
        return EEnum(location, name, signature, entries)
    }

    private fun castEnumEntryFromEnumNameDeclaration(
        ctx: SystemVerilogParser.EnumNameDeclarationContext,
        castContext: CastContext
    ): EEnumEntry {
        val identifier = ctx.enumIdentifier()
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        return EEnumEntry(location, name)
    }

    private fun castStructFromDataTypeStruct(
        location: SourceLocation,
        name: String,
        signature: String,
        dataTypeStruct: SystemVerilogParser.DataTypeStructContext,
        castContext: CastContext
    ): EStruct {
        val entries = dataTypeStruct.structUnionMember().flatMap {
            castStructEntriesFromStructUnionMember(it, castContext)
        }
        return EStruct(
            location,
            name,
            signature,
            entries
        )
    }

    private fun castStructEntriesFromStructUnionMember(
        ctx: SystemVerilogParser.StructUnionMemberContext,
        castContext: CastContext
    ): List<EStructEntry> {
        val descriptor = castContext.castDescriptor(ctx.dataTypeOrVoid())
        val identifiers = ctx.listOfVariableDeclAssignments()
            .variableDeclAssignment()
            .filterIsInstance<SystemVerilogParser.VariableDeclAssignmentVariableContext>()
            .map { it.variableIdentifier() }
        return identifiers.map {
            val location = castContext.getLocation(it)
            val name = it.text
            EStructEntry(location, name, ElementCopier.deepCopy(descriptor))
        }
    }
}
