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
import io.verik.importer.ast.element.common.EContainerElement
import io.verik.importer.ast.element.declaration.ESvValueParameter
import io.verik.importer.cast.common.CastContext
import io.verik.importer.common.ElementCopier

object ValueParameterCaster {

    fun castValueParameterFromTfPortItem(
        ctx: SystemVerilogParser.TfPortItemContext,
        castContext: CastContext
    ): ESvValueParameter? {
        val identifier = ctx.portIdentifier()
        val location = castContext.getLocation(identifier)
        val name = identifier.text
        val descriptor = castContext.castDescriptor(ctx.dataTypeOrImplicit()) ?: return null
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
    ): EContainerElement? {
        val descriptor = castContext.castDescriptor(ctx.dataTypeOrImplicit()) ?: return null
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
