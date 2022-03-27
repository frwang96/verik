/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.common

import io.verik.importer.ast.element.common.EContainerElement
import io.verik.importer.ast.element.common.EElement
import io.verik.importer.ast.element.declaration.EDeclaration
import io.verik.importer.ast.element.declaration.EPort
import io.verik.importer.ast.element.declaration.ESvValueParameter
import io.verik.importer.ast.element.declaration.ETypeParameter
import io.verik.importer.ast.element.descriptor.EDescriptor
import io.verik.importer.ast.element.descriptor.ENothingDescriptor
import io.verik.importer.ast.element.descriptor.ETypeArgument
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
        return castElements(ctx).map { it.cast() }
    }

    fun castTypeParameters(ctx: RuleContext): List<ETypeParameter> {
        return castElements(ctx).map { it.cast() }
    }

    fun castValueParameters(ctx: RuleContext): List<ESvValueParameter> {
        return castElements(ctx).map { it.cast() }
    }

    fun castPort(ctx: RuleContext): EPort? {
        return casterVisitor.getElement(ctx)
    }

    fun castDescriptor(ctx: RuleContext): EDescriptor {
        val descriptor = casterVisitor.getElement<EDescriptor>(ctx)
        return if (descriptor != null) {
            descriptor
        } else {
            val location = getLocation(ctx)
            Messages.UNABLE_TO_CAST.on(location, "type")
            ENothingDescriptor(location)
        }
    }

    fun castTypeArgument(ctx: RuleContext): ETypeArgument? {
        return casterVisitor.getElement(ctx)
    }

    fun castTypeArguments(ctx: RuleContext): List<ETypeArgument> {
        return castElements(ctx).map { it.cast() }
    }

    private fun castElements(ctx: RuleContext): List<EElement> {
        return when (val element = casterVisitor.getElement<EElement>(ctx)) {
            null -> listOf()
            is EContainerElement -> element.elements
            else -> listOf(element)
        }
    }
}
