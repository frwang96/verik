/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.ast.element.declaration.ESvClass
import io.verik.importer.ast.element.descriptor.ESimpleDescriptor
import io.verik.importer.cast.common.CastContext
import io.verik.importer.cast.common.SignatureBuilder
import io.verik.importer.core.Core

/**
 * Caster for SystemVerilog class declarations.
 */
object ClassCaster {

    fun castClassFromClassDeclaration(
        ctx: SystemVerilogParser.ClassDeclarationContext,
        castContext: CastContext
    ): ESvClass {
        val location = castContext.getLocation(ctx.CLASS())
        val name = ctx.classIdentifier()[0].text
        val signature = SignatureBuilder.buildSignature(ctx, name)
        val declarations = ctx.classItem().flatMap { castContext.castDeclarations(it) }
        val typeParameters = ctx.parameterPortList()?.let { castContext.castTypeParameters(it) } ?: listOf()
        val superDescriptor = ctx.classType()
            ?.let { castContext.castDescriptor(it) }
            ?: ESimpleDescriptor(location, Core.C_Class.toType())
        return ESvClass(
            location,
            name,
            signature,
            ArrayList(declarations),
            typeParameters,
            superDescriptor
        )
    }
}
