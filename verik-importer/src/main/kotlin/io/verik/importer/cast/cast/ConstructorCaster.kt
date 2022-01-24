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
import io.verik.importer.ast.sv.element.declaration.SvConstructor
import io.verik.importer.ast.sv.element.declaration.SvDeclaration
import io.verik.importer.cast.common.CastContext
import io.verik.importer.cast.common.SignatureBuilder

object ConstructorCaster {

    fun castConstructorFromClassMethodConstructor(
        ctx: SystemVerilogParser.ClassMethodConstructorContext,
        castContext: CastContext
    ): SvDeclaration? {
        val declaration = castContext.castDeclaration(ctx.classConstructorDeclaration()) ?: return null
        val signature = SignatureBuilder.buildSignature(ctx, declaration.name)
        declaration.signature = signature
        return declaration
    }

    fun castConstructorFromClassConstructorDeclaration(
        ctx: SystemVerilogParser.ClassConstructorDeclarationContext,
        castContext: CastContext
    ): SvConstructor? {
        val location = castContext.getLocation(ctx.NEW()[0])
        val tfPortItems = ctx.tfPortList()?.tfPortItem() ?: listOf()
        val signature = SignatureBuilder.buildSignature(ctx, "new")
        val valueParameters = tfPortItems.map { castContext.castValueParameter(it) ?: return null }
        return SvConstructor(
            location,
            signature,
            valueParameters
        )
    }
}
