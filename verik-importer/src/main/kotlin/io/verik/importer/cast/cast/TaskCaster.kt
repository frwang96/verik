/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.ast.element.declaration.EDeclaration
import io.verik.importer.ast.element.declaration.ETask
import io.verik.importer.cast.common.CastContext
import io.verik.importer.cast.common.SignatureBuilder

/**
 * Caster for SystemVerilog task declarations.
 */
object TaskCaster {

    fun castTaskFromClassMethodTask(
        ctx: SystemVerilogParser.ClassMethodTaskContext,
        castContext: CastContext
    ): ETask? {
        val task = castContext.castDeclaration(ctx.taskDeclaration())?.cast<ETask>() ?: return null
        val signature = SignatureBuilder.buildSignature(ctx, task.name)
        val isStatic = ctx.methodQualifier().any { it.text == "static" }
        task.signature = signature
        task.isStatic = isStatic
        return task
    }

    fun castTaskFromTaskDeclaration(
        ctx: SystemVerilogParser.TaskDeclarationContext,
        castContext: CastContext
    ): EDeclaration? {
        val declaration = castContext.castDeclaration(ctx.taskBodyDeclaration()) ?: return null
        val signature = SignatureBuilder.buildSignature(ctx, declaration.name)
        declaration.signature = signature
        return declaration
    }

    fun castTaskFromTaskBodyDeclarationNoPortList(
        ctx: SystemVerilogParser.TaskBodyDeclarationNoPortListContext,
        castContext: CastContext
    ): ETask? {
        if (ctx.classScope() != null)
            return null
        val identifier = ctx.taskIdentifier()[0]
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val tfPortDeclarations = ctx.tfItemDeclaration().mapNotNull { it.tfPortDeclaration() }
        val valueParameters = tfPortDeclarations.flatMap { castContext.castValueParameters(it) }
        return ETask(
            location,
            name,
            null,
            valueParameters,
            false
        )
    }

    fun castTaskFromTaskBodyDeclarationPortList(
        ctx: SystemVerilogParser.TaskBodyDeclarationPortListContext,
        castContext: CastContext
    ): ETask? {
        if (ctx.classScope() != null)
            return null
        val identifier = ctx.taskIdentifier()[0]
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val tfPortItems = ctx.tfPortList()?.tfPortItem() ?: listOf()
        val valueParameters = tfPortItems.flatMap { castContext.castValueParameters(it) }
        return ETask(
            location,
            name,
            null,
            valueParameters,
            false
        )
    }

    fun castTaskFromTaskPrototype(ctx: SystemVerilogParser.TaskPrototypeContext, castContext: CastContext): ETask {
        val identifier = ctx.taskIdentifier()
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val signature = SignatureBuilder.buildSignature(ctx, name)
        val tfPortItems = ctx.tfPortList()?.tfPortItem() ?: listOf()
        val valueParameters = tfPortItems.flatMap { castContext.castValueParameters(it) }
        return ETask(
            location,
            name,
            signature,
            valueParameters,
            false
        )
    }
}
