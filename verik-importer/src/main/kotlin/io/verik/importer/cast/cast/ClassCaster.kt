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
import io.verik.importer.ast.element.declaration.ESvClass
import io.verik.importer.ast.element.descriptor.ESimpleDescriptor
import io.verik.importer.cast.common.CastContext
import io.verik.importer.cast.common.SignatureBuilder
import io.verik.importer.core.Core

object ClassCaster {

    fun castClassFromClassDeclaration(
        ctx: SystemVerilogParser.ClassDeclarationContext,
        castContext: CastContext
    ): ESvClass? {
        val location = castContext.getLocation(ctx.CLASS())
        val name = ctx.classIdentifier()[0].text
        val signature = SignatureBuilder.buildSignature(ctx, name)
        val declarations = ctx.classItem().flatMap { castContext.castDeclarations(it) }
        val typeParameters = ctx.parameterPortList()?.let { castContext.castTypeParameters(it) } ?: listOf()
        val superDescriptor = ctx.classType()
            ?.let { castContext.castDescriptor(it) ?: return null }
            ?: ESimpleDescriptor(location, Core.C_Any.toType())
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
