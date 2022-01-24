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

import io.verik.importer.ast.sv.element.common.SvContainerElement
import io.verik.importer.ast.sv.element.common.SvElement
import io.verik.importer.ast.sv.element.declaration.SvDeclaration
import io.verik.importer.ast.sv.element.declaration.SvPort
import io.verik.importer.ast.sv.element.declaration.SvProperty
import io.verik.importer.ast.sv.element.declaration.SvValueParameter
import io.verik.importer.ast.sv.element.descriptor.SvDescriptor
import io.verik.importer.ast.sv.element.expression.SvExpression
import io.verik.importer.ast.sv.element.expression.SvNothingExpression
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

    fun castDeclaration(ctx: RuleContext): SvDeclaration? {
        return when (val element = casterVisitor.getElement<SvElement>(ctx)) {
            null -> null
            is SvContainerElement -> {
                if (element.elements.size != 1)
                    Messages.INTERNAL_ERROR.on(getLocation(ctx), "Single declaration expected")
                element.elements.firstOrNull()?.cast()
            }
            else -> element.cast()
        }
    }

    fun castDeclarations(ctx: RuleContext): List<SvDeclaration> {
        return when (val element = casterVisitor.getElement<SvElement>(ctx)) {
            null -> listOf()
            is SvContainerElement -> element.elements.map { it.cast() }
            else -> listOf(element.cast())
        }
    }

    fun castProperties(ctx: RuleContext): List<SvProperty> {
        return castDeclarations(ctx).map { it.cast() }
    }

    fun castValueParameter(ctx: RuleContext): SvValueParameter? {
        return casterVisitor.getElement(ctx)
    }

    fun castPort(ctx: RuleContext): SvPort? {
        return casterVisitor.getElement(ctx)
    }

    fun castDescriptor(ctx: RuleContext): SvDescriptor? {
        return casterVisitor.getElement(ctx)
    }

    fun castExpression(ctx: RuleContext): SvExpression {
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
