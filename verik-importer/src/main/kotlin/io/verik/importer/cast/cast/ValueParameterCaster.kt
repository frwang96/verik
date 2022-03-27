/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.ast.element.common.EContainerElement
import io.verik.importer.ast.element.declaration.ESvValueParameter
import io.verik.importer.cast.common.CastContext
import io.verik.importer.common.ElementCopier

object ValueParameterCaster {

    fun castValueParameterFromTfPortItem(
        ctx: SystemVerilogParser.TfPortItemContext,
        castContext: CastContext
    ): ESvValueParameter {
        val identifier = ctx.portIdentifier()
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val descriptor = castContext.castDescriptor(ctx.dataTypeOrImplicit())
        val hasDefault = ctx.expression() != null
        return ESvValueParameter(
            location,
            name,
            descriptor,
            hasDefault
        )
    }

    fun castValueParameterFromTfPortDeclaration(
        ctx: SystemVerilogParser.TfPortDeclarationContext,
        castContext: CastContext
    ): EContainerElement {
        val descriptor = castContext.castDescriptor(ctx.dataTypeOrImplicit())
        val tfVariableIdentifiers = ctx.listOfTfVariableIdentifiers().tfVariableIdentifier()
        val valueParameters = tfVariableIdentifiers.map {
            val identifier = it.portIdentifier()
            val location = castContext.getLocation(identifier)
            val name = identifier.text
            val hasDefault = it.expression() != null
            ESvValueParameter(
                location,
                name,
                ElementCopier.deepCopy(descriptor),
                hasDefault
            )
        }
        return EContainerElement(castContext.getLocation(ctx), valueParameters)
    }
}
