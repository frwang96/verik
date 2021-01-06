/*
 * Copyright (c) 2020 Francis Wang
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

package verikc.ps.ast

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.TypeReified
import verikc.base.symbol.Symbol
import verikc.ge.ast.GeParameterProperty
import verikc.ge.ast.GePrimaryProperty

data class PsPrimaryProperty(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    override val typeReified: TypeReified
): PsProperty {

    companion object {

        operator fun invoke(primaryProperty: GePrimaryProperty): PsPrimaryProperty {
            val typeReified = primaryProperty.typeReified
                ?: throw LineException("property ${primaryProperty.symbol} has not been reified", primaryProperty.line)

            return PsPrimaryProperty(
                primaryProperty.line,
                primaryProperty.identifier,
                primaryProperty.symbol,
                typeReified
            )
        }

        operator fun invoke(parameterProperty: GeParameterProperty): PsPrimaryProperty {
            val typeReified = parameterProperty.typeReified
                ?: throw LineException(
                    "property ${parameterProperty.symbol} has not been reified",
                    parameterProperty.line
                )

            return PsPrimaryProperty(
                parameterProperty.line,
                parameterProperty.identifier,
                parameterProperty.symbol,
                typeReified
            )
        }
    }
}
