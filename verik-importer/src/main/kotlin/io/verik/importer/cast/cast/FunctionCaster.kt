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
import io.verik.importer.ast.sv.element.declaration.SvDeclaration
import io.verik.importer.ast.sv.element.declaration.SvFunction
import io.verik.importer.cast.common.CastContext
import io.verik.importer.cast.common.SignatureBuilder
import io.verik.importer.common.Type

object FunctionCaster {

    fun castTaskFromClassMethodFunction(
        ctx: SystemVerilogParser.ClassMethodFunctionContext,
        castContext: CastContext
    ): SvDeclaration? {
        val declaration = castContext.castDeclaration(ctx.functionDeclaration()) ?: return null
        val signature = SignatureBuilder.buildSignature(ctx, declaration.name)
        declaration.signature = signature
        return declaration
    }

    fun castFunctionFromFunctionDeclaration(
        ctx: SystemVerilogParser.FunctionDeclarationContext,
        castContext: CastContext
    ): SvDeclaration? {
        val declaration = castContext.castDeclaration(ctx.functionBodyDeclaration()) ?: return null
        val signature = SignatureBuilder.buildSignature(ctx, declaration.name)
        declaration.signature = signature
        return declaration
    }

    fun castFunctionFromFunctionBodyDeclarationNoPortList(
        ctx: SystemVerilogParser.FunctionBodyDeclarationNoPortListContext,
        castContext: CastContext
    ): SvFunction? {
        val identifier = ctx.functionIdentifier()[0]
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val descriptor = castContext.castDescriptor(ctx.functionDataTypeOrImplicit()) ?: return null
        return SvFunction(
            location,
            name,
            null,
            Type.unresolved(),
            listOf(),
            descriptor
        )
    }

    fun castFunctionFromFunctionBodyDeclarationPortList(
        ctx: SystemVerilogParser.FunctionBodyDeclarationPortListContext,
        castContext: CastContext
    ): SvFunction? {
        val identifier = ctx.functionIdentifier()[0]
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val descriptor = castContext.castDescriptor(ctx.functionDataTypeOrImplicit()) ?: return null
        val tfPortItems = ctx.tfPortList()?.tfPortItem() ?: listOf()
        val valueParameters = tfPortItems.map { castContext.castValueParameter(it) ?: return null }
        return SvFunction(
            location,
            name,
            null,
            Type.unresolved(),
            valueParameters,
            descriptor
        )
    }
}
