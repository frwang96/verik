/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.ast.element.declaration.EDeclaration
import io.verik.importer.ast.element.declaration.ESvFunction
import io.verik.importer.ast.element.declaration.ETask
import io.verik.importer.cast.common.CastContext
import io.verik.importer.cast.common.SignatureBuilder

object FunctionCaster {

    fun castFunctionFromClassMethodFunction(
        ctx: SystemVerilogParser.ClassMethodFunctionContext,
        castContext: CastContext
    ): ESvFunction? {
        val function = castContext.castDeclaration(ctx.functionDeclaration())?.cast<ESvFunction>() ?: return null
        val signature = SignatureBuilder.buildSignature(ctx, function.name)
        val isStatic = ctx.methodQualifier().any { it.text == "static" }
        function.signature = signature
        function.isStatic = isStatic
        return function
    }

    fun castFunctionFromClassMethodExternMethod(
        ctx: SystemVerilogParser.ClassMethodExternMethodContext,
        castContext: CastContext
    ): EDeclaration? {
        val declaration = castContext.castDeclaration(ctx.methodPrototype()) ?: return null
        val signature = SignatureBuilder.buildSignature(ctx, declaration.name)
        val isStatic = ctx.methodQualifier().any { it.text == "static" }
        declaration.signature = signature
        when (declaration) {
            is ESvFunction -> declaration.isStatic = isStatic
            is ETask -> declaration.isStatic = isStatic
        }
        return declaration
    }

    fun castFunctionFromFunctionDeclaration(
        ctx: SystemVerilogParser.FunctionDeclarationContext,
        castContext: CastContext
    ): EDeclaration? {
        val declaration = castContext.castDeclaration(ctx.functionBodyDeclaration()) ?: return null
        val signature = SignatureBuilder.buildSignature(ctx, declaration.name)
        declaration.signature = signature
        return declaration
    }

    fun castFunctionFromFunctionBodyDeclarationNoPortList(
        ctx: SystemVerilogParser.FunctionBodyDeclarationNoPortListContext,
        castContext: CastContext
    ): ESvFunction? {
        if (ctx.classScope() != null)
            return null
        val identifier = ctx.functionIdentifier()[0]
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val tfPortDeclarations = ctx.tfItemDeclaration().mapNotNull { it.tfPortDeclaration() }
        val valueParameters = tfPortDeclarations.flatMap { castContext.castValueParameters(it) }
        val descriptor = castContext.castDescriptor(ctx.functionDataTypeOrImplicit())
        return ESvFunction(
            location,
            name,
            null,
            valueParameters,
            false,
            descriptor
        )
    }

    fun castFunctionFromFunctionBodyDeclarationPortList(
        ctx: SystemVerilogParser.FunctionBodyDeclarationPortListContext,
        castContext: CastContext
    ): ESvFunction? {
        if (ctx.classScope() != null)
            return null
        val identifier = ctx.functionIdentifier()[0]
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val descriptor = castContext.castDescriptor(ctx.functionDataTypeOrImplicit())
        val tfPortItems = ctx.tfPortList()?.tfPortItem() ?: listOf()
        val valueParameters = tfPortItems.flatMap { castContext.castValueParameters(it) }
        return ESvFunction(
            location,
            name,
            null,
            valueParameters,
            false,
            descriptor
        )
    }

    fun castFunctionFromFunctionPrototype(
        ctx: SystemVerilogParser.FunctionPrototypeContext,
        castContext: CastContext
    ): ESvFunction {
        val identifier = ctx.functionIdentifier()
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val signature = SignatureBuilder.buildSignature(ctx, name)
        val descriptor = castContext.castDescriptor(ctx.dataTypeOrVoid())
        val tfPortItems = ctx.tfPortList()?.tfPortItem() ?: listOf()
        val valueParameters = tfPortItems.flatMap { castContext.castValueParameters(it) }
        return ESvFunction(
            location,
            name,
            signature,
            valueParameters,
            false,
            descriptor
        )
    }
}
