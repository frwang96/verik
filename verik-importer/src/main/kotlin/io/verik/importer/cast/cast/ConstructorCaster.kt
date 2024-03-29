/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.ast.element.declaration.EDeclaration
import io.verik.importer.ast.element.declaration.ESvConstructor
import io.verik.importer.cast.common.CastContext
import io.verik.importer.cast.common.SignatureBuilder

/**
 * Caster for SystemVerilog constructor declarations.
 */
object ConstructorCaster {

    fun castConstructorFromClassMethodConstructor(
        ctx: SystemVerilogParser.ClassMethodConstructorContext,
        castContext: CastContext
    ): EDeclaration? {
        val declaration = castContext.castDeclaration(ctx.classConstructorDeclaration()) ?: return null
        val signature = SignatureBuilder.buildSignature(ctx, declaration.name)
        declaration.signature = signature
        return declaration
    }

    fun castConstructorFromClassMethodExternConstructorContext(
        ctx: SystemVerilogParser.ClassMethodExternConstructorContext,
        castContext: CastContext
    ): EDeclaration {
        val location = castContext.getLocation(ctx.classConstructorPrototype().NEW())
        val signature = SignatureBuilder.buildSignature(ctx, "new")
        val tfPortItems = ctx.classConstructorPrototype().tfPortList()?.tfPortItem() ?: listOf()
        val valueParameters = tfPortItems.flatMap { castContext.castValueParameters(it) }
        return ESvConstructor(
            location,
            signature,
            valueParameters
        )
    }

    fun castConstructorFromClassConstructorDeclaration(
        ctx: SystemVerilogParser.ClassConstructorDeclarationContext,
        castContext: CastContext
    ): ESvConstructor? {
        if (ctx.classScope() != null)
            return null
        val location = castContext.getLocation(ctx.NEW()[0])
        val signature = SignatureBuilder.buildSignature(ctx, "new")
        val tfPortItems = ctx.tfPortList()?.tfPortItem() ?: listOf()
        val valueParameters = tfPortItems.flatMap { castContext.castValueParameters(it) }
        return ESvConstructor(
            location,
            signature,
            valueParameters
        )
    }
}
