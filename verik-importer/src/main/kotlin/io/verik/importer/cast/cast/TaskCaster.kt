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
import io.verik.importer.ast.sv.element.declaration.SvTask
import io.verik.importer.cast.common.CastContext
import io.verik.importer.cast.common.SignatureBuilder

object TaskCaster {

    fun castTaskFromClassMethodTask(
        ctx: SystemVerilogParser.ClassMethodTaskContext,
        castContext: CastContext
    ): SvDeclaration? {
        val declaration = castContext.castDeclaration(ctx.taskDeclaration()) ?: return null
        val signature = SignatureBuilder.buildSignature(ctx, declaration.name)
        declaration.signature = signature
        return declaration
    }

    fun castTaskFromTaskDeclaration(
        ctx: SystemVerilogParser.TaskDeclarationContext,
        castContext: CastContext
    ): SvDeclaration? {
        val declaration = castContext.castDeclaration(ctx.taskBodyDeclaration()) ?: return null
        val signature = SignatureBuilder.buildSignature(ctx, declaration.name)
        declaration.signature = signature
        return declaration
    }

    fun castTaskFromTaskBodyDeclarationNoPortList(
        ctx: SystemVerilogParser.TaskBodyDeclarationNoPortListContext,
        castContext: CastContext
    ): SvTask? {
        if (ctx.classScope() != null)
            return null
        val identifier = ctx.taskIdentifier()[0]
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val tfPortDeclarations = ctx.tfItemDeclaration().mapNotNull { it.tfPortDeclaration() }
        val valueParameters = tfPortDeclarations.flatMap { castContext.castValueParameters(it) }
        return SvTask(location, name, null, valueParameters)
    }

    fun castTaskFromTaskBodyDeclarationPortList(
        ctx: SystemVerilogParser.TaskBodyDeclarationPortListContext,
        castContext: CastContext
    ): SvTask? {
        if (ctx.classScope() != null)
            return null
        val identifier = ctx.taskIdentifier()[0]
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val tfPortItems = ctx.tfPortList()?.tfPortItem() ?: listOf()
        val valueParameters = tfPortItems.flatMap { castContext.castValueParameters(it) }
        return SvTask(location, name, null, valueParameters)
    }

    fun castTaskFromTaskPrototype(ctx: SystemVerilogParser.TaskPrototypeContext, castContext: CastContext): SvTask {
        val identifier = ctx.taskIdentifier()
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val signature = SignatureBuilder.buildSignature(ctx, name)
        val tfPortItems = ctx.tfPortList()?.tfPortItem() ?: listOf()
        val valueParameters = tfPortItems.flatMap { castContext.castValueParameters(it) }
        return SvTask(location, name, signature, valueParameters)
    }
}
