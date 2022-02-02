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

package io.verik.importer.cast.common

import io.verik.importer.ast.element.common.EContainerElement
import io.verik.importer.ast.element.common.EElement
import io.verik.importer.ast.element.declaration.EDeclaration
import io.verik.importer.ast.element.declaration.EPort
import io.verik.importer.ast.element.declaration.ESvTypeParameter
import io.verik.importer.ast.element.declaration.ESvValueParameter
import io.verik.importer.ast.element.descriptor.EDescriptor
import io.verik.importer.ast.element.expression.EExpression
import io.verik.importer.ast.element.expression.ENothingExpression
import io.verik.importer.message.Messages
import io.verik.importer.message.SourceLocation
import org.antlr.v4.runtime.RuleContext
import org.antlr.v4.runtime.TokenStream
import org.antlr.v4.runtime.tree.TerminalNode

class CastContext(
    private val parserTokenStream: TokenStream
) {

    private val casterVisitor = CasterVisitor(this)

    fun getLocation(ctx: RuleContext): SourceLocation {
        val index = ctx.sourceInterval.a
        return SourceLocation.get(parserTokenStream.get(index))
    }

    fun getLocation(terminalNode: TerminalNode): SourceLocation {
        return SourceLocation.get(terminalNode)
    }

    fun castDeclaration(ctx: RuleContext): EDeclaration? {
        return when (val element = casterVisitor.getElement<EElement>(ctx)) {
            null -> null
            is EContainerElement -> {
                if (element.elements.size != 1)
                    Messages.INTERNAL_ERROR.on(getLocation(ctx), "Single declaration expected")
                element.elements.firstOrNull()?.cast()
            }
            else -> element.cast()
        }
    }

    fun castDeclarations(ctx: RuleContext): List<EDeclaration> {
        return when (val element = casterVisitor.getElement<EElement>(ctx)) {
            null -> listOf()
            is EContainerElement -> element.elements.map { it.cast() }
            else -> listOf(element.cast())
        }
    }

    fun castTypeParameters(ctx: RuleContext): List<ESvTypeParameter> {
        return castDeclarations(ctx).map { it.cast() }
    }

    fun castValueParameters(ctx: RuleContext): List<ESvValueParameter> {
        return castDeclarations(ctx).map { it.cast() }
    }

    fun castPort(ctx: RuleContext): EPort? {
        return casterVisitor.getElement(ctx)
    }

    fun castDescriptor(ctx: RuleContext): EDescriptor? {
        return casterVisitor.getElement(ctx)
    }

    fun castExpression(ctx: RuleContext): EExpression {
        val element = casterVisitor.getElement<EExpression>(ctx)
        return if (element != null) {
            element
        } else {
            val location = getLocation(ctx)
            Messages.UNABLE_TO_CAST.on(location, "expression")
            ENothingExpression(location)
        }
    }
}
