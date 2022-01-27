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
import io.verik.importer.ast.element.expression.ELiteralExpression
import io.verik.importer.ast.element.expression.EReferenceExpression
import io.verik.importer.cast.common.CastContext
import io.verik.importer.core.Core

object ExpressionCaster {

    fun castLiteralExpressionFromConstantPrimaryLiteral(
        ctx: SystemVerilogParser.ConstantPrimaryLiteralContext,
        castContext: CastContext
    ): ELiteralExpression {
        val location = castContext.getLocation(ctx)
        return ELiteralExpression(location, ctx.text)
    }

    fun castReferenceExpressionFromConstantPrimaryParameter(
        ctx: SystemVerilogParser.ConstantPrimaryParameterContext,
        castContext: CastContext
    ): EReferenceExpression {
        val location = castContext.getLocation(ctx)
        val identifier = ctx.psParameterIdentifier().parameterIdentifier()
        val name = identifier.text
        return EReferenceExpression(location, name, Core.C_Nothing)
    }
}
