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
import io.verik.importer.ast.element.declaration.EDeclaration
import io.verik.importer.ast.element.declaration.EFunction
import io.verik.importer.cast.common.CastContext
import io.verik.importer.cast.common.SignatureBuilder

object FunctionCaster {

    fun castFunctionFromClassMethodFunction(
        ctx: SystemVerilogParser.ClassMethodFunctionContext,
        castContext: CastContext
    ): EDeclaration? {
        val declaration = castContext.castDeclaration(ctx.functionDeclaration()) ?: return null
        val signature = SignatureBuilder.buildSignature(ctx, declaration.name)
        declaration.signature = signature
        return declaration
    }

    fun castFunctionFromClassMethodExternMethod(
        ctx: SystemVerilogParser.ClassMethodExternMethodContext,
        castContext: CastContext
    ): EDeclaration? {
        val declaration = castContext.castDeclaration(ctx.methodPrototype()) ?: return null
        val signature = SignatureBuilder.buildSignature(ctx, declaration.name)
        declaration.signature = signature
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
    ): EFunction? {
        if (ctx.classScope() != null)
            return null
        val identifier = ctx.functionIdentifier()[0]
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val tfPortDeclarations = ctx.tfItemDeclaration().mapNotNull { it.tfPortDeclaration() }
        val valueParameters = tfPortDeclarations.flatMap { castContext.castValueParameters(it) }
        val descriptor = castContext.castDescriptor(ctx.functionDataTypeOrImplicit()) ?: return null
        return EFunction(
            location,
            name,
            null,
            valueParameters,
            descriptor
        )
    }

    fun castFunctionFromFunctionBodyDeclarationPortList(
        ctx: SystemVerilogParser.FunctionBodyDeclarationPortListContext,
        castContext: CastContext
    ): EFunction? {
        if (ctx.classScope() != null)
            return null
        val identifier = ctx.functionIdentifier()[0]
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val descriptor = castContext.castDescriptor(ctx.functionDataTypeOrImplicit()) ?: return null
        val tfPortItems = ctx.tfPortList()?.tfPortItem() ?: listOf()
        val valueParameters = tfPortItems.flatMap { castContext.castValueParameters(it) }
        return EFunction(
            location,
            name,
            null,
            valueParameters,
            descriptor
        )
    }

    fun castFunctionFromFunctionPrototype(
        ctx: SystemVerilogParser.FunctionPrototypeContext,
        castContext: CastContext
    ): EFunction? {
        val identifier = ctx.functionIdentifier()
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val signature = SignatureBuilder.buildSignature(ctx, name)
        val descriptor = castContext.castDescriptor(ctx.dataTypeOrVoid()) ?: return null
        val tfPortItems = ctx.tfPortList()?.tfPortItem() ?: listOf()
        val valueParameters = tfPortItems.flatMap { castContext.castValueParameters(it) }
        return EFunction(
            location,
            name,
            signature,
            valueParameters,
            descriptor
        )
    }
}
