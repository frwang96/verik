/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.ast.element.declaration.EDeclaration
import io.verik.importer.ast.element.declaration.EModule
import io.verik.importer.ast.element.declaration.EPort
import io.verik.importer.cast.common.CastContext
import io.verik.importer.cast.common.SignatureBuilder
import io.verik.importer.message.Messages

object ModuleCaster {

    fun castModuleFromModuleDeclarationNonAnsi(
        ctx: SystemVerilogParser.ModuleDeclarationNonAnsiContext,
        castContext: CastContext
    ): EModule? {
        val identifier = ctx.moduleNonAnsiHeader().moduleIdentifier()
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val signature = SignatureBuilder.buildSignature(ctx, name)
        val declarations = ArrayList<EDeclaration>()
        val typeParameters = ctx.moduleNonAnsiHeader().parameterPortList()
            ?.let { castContext.castTypeParameters(it) }
            ?: listOf()
        val ports = ArrayList<EPort>()
        ctx.moduleItem().forEach {
            if (it is SystemVerilogParser.ModuleItemPortDeclarationContext) {
                val port = castContext.castPort(it)
                if (port == null) {
                    Messages.UNABLE_TO_CAST.on(castContext.getLocation(it), "port")
                    return null
                }
                ports.add(port)
            } else {
                declarations.addAll(castContext.castDeclarations(it))
            }
        }
        return EModule(
            location,
            name,
            signature,
            declarations,
            typeParameters,
            ports
        )
    }

    fun castModuleFromModuleDeclarationAnsi(
        ctx: SystemVerilogParser.ModuleDeclarationAnsiContext,
        castContext: CastContext
    ): EModule? {
        val moduleAnsiHeader = ctx.moduleAnsiHeader()
        val identifier = moduleAnsiHeader.moduleIdentifier()
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val signature = SignatureBuilder.buildSignature(ctx, name)
        val declarations = ctx.nonPortModuleItem().flatMap { castContext.castDeclarations(it) }
        val typeParameters = ctx.moduleAnsiHeader().parameterPortList()
            ?.let { castContext.castTypeParameters(it) }
            ?: listOf()
        val ports = moduleAnsiHeader.listOfPortDeclarations()?.ansiPortDeclaration()?.map {
            castContext.castPort(it) ?: return null
        } ?: listOf()
        return EModule(
            location,
            name,
            signature,
            ArrayList(declarations),
            typeParameters,
            ArrayList(ports)
        )
    }
}
