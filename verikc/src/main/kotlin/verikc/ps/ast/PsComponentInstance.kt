/*
 * Copyright 2020 Francis Wang
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
import verikc.base.ast.ReifiedType
import verikc.base.ast.Symbol
import verikc.ps.symbol.PsSymbolTable
import verikc.rf.ast.RfComponentInstance
import verikc.sv.ast.SvComponentInstance

data class PsComponentInstance(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    override val reifiedType: ReifiedType,
    val connections: List<PsConnection>
): PsProperty {

    fun extract(symbolTable: PsSymbolTable): SvComponentInstance {
        return SvComponentInstance(
            line,
            identifier,
            symbolTable.extractTypeIdentifier(reifiedType.typeSymbol, line),
            connections.map { it.extract(symbolTable) }
        )
    }

    companion object {

        operator fun invoke(componentInstance: RfComponentInstance): PsComponentInstance {
            val reifiedType = componentInstance.reifiedType
                ?: throw LineException(
                    "component instance ${componentInstance.symbol} has not been reified",
                    componentInstance.line
                )

            return PsComponentInstance(
                componentInstance.line,
                componentInstance.identifier,
                componentInstance.symbol,
                reifiedType,
                componentInstance.connections.map { PsConnection(it) }
            )
        }
    }
}
