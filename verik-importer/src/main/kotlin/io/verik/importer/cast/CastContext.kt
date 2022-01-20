/*
 * Copyright (c) 2021 Francis Wang
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

package io.verik.importer.cast

import io.verik.importer.ast.sv.element.declaration.SvDeclaration
import io.verik.importer.ast.sv.element.declaration.SvPort
import io.verik.importer.ast.sv.element.descriptor.SvDescriptor
import io.verik.importer.ast.sv.element.expression.SvExpression
import io.verik.importer.ast.sv.element.expression.SvNothingExpression
import io.verik.importer.message.Messages
import io.verik.importer.message.SourceLocation
import org.antlr.v4.runtime.RuleContext
import org.antlr.v4.runtime.TokenStream

class CastContext(
    private val parserTokenStream: TokenStream
) {

    private val casterVisitor = CasterVisitor(this)

    fun getLocation(ctx: RuleContext): SourceLocation {
        val index = ctx.sourceInterval.a
        return SourceLocation.get(parserTokenStream.get(index))
    }

    fun getDeclaration(ctx: RuleContext): SvDeclaration? {
        return casterVisitor.getElement(ctx)
    }

    fun getPort(ctx: RuleContext): SvPort? {
        return casterVisitor.getElement(ctx)
    }

    fun getDescriptor(ctx: RuleContext): SvDescriptor? {
        return casterVisitor.getElement(ctx)
    }

    fun getExpression(ctx: RuleContext): SvExpression {
        val element = casterVisitor.getElement<SvExpression>(ctx)
        return if (element != null) {
            element
        } else {
            val location = getLocation(ctx)
            Messages.UNABLE_TO_CAST.on(location, "expression")
            SvNothingExpression(location)
        }
    }
}
