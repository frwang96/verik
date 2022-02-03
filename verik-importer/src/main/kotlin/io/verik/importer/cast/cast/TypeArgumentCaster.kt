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
import io.verik.importer.ast.element.descriptor.EDescriptorTypeArgument
import io.verik.importer.ast.element.descriptor.ETypeArgument
import io.verik.importer.cast.common.CastContext

object TypeArgumentCaster {

    fun castTypeArgumentsFromListOfParameterAssignmentsOrdered(
        ctx: SystemVerilogParser.ListOfParameterAssignmentsOrderedContext,
        castContext: CastContext
    ): EContainerElement? {
        val typeArguments = ctx.orderedParameterAssignment().map { castContext.castTypeArgument(it) ?: return null }
        return EContainerElement(
            castContext.getLocation(ctx),
            typeArguments
        )
    }

    fun castTypeArgumentFromParamExpression(
        ctx: SystemVerilogParser.ParamExpressionContext,
        castContext: CastContext
    ): ETypeArgument? {
        val dataType = ctx.dataType()
        return if (dataType != null) {
            val descriptor = castContext.castDescriptor(dataType) ?: return null
            EDescriptorTypeArgument(
                descriptor.location,
                null,
                descriptor
            )
        } else null
    }
}
