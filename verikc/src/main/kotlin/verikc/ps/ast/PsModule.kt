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
import verikc.base.ast.Symbol
import verikc.ps.extract.PsIdentifierExtractorUtil
import verikc.ps.symbol.PsSymbolTable
import verikc.rf.ast.RfModule
import verikc.sv.ast.SvModule

data class PsModule(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    val ports: List<PsPort>,
    val primaryProperties: List<PsPrimaryProperty>,
    val componentInstances: List<PsComponentInstance>,
    val actionBlocks: List<PsActionBlock>
): PsDeclaration {

    fun extract(symbolTable: PsSymbolTable): SvModule {
        return SvModule(
            line,
            PsIdentifierExtractorUtil.identifierWithoutUnderscore(this),
            ports.map { it.extract(symbolTable) },
            primaryProperties.map { it.extract(symbolTable) },
            componentInstances.map { it.extract(symbolTable) },
            actionBlocks.map { it.extract(symbolTable) }
        )
    }

    constructor(module: RfModule): this(
        module.line,
        module.identifier,
        module.symbol,
        module.ports.map { PsPort(it) },
        module.primaryProperties.map { PsPrimaryProperty(it) },
        module.componentInstances.map { PsComponentInstance(it) },
        module.actionBlocks.map { PsActionBlock(it) }
    )
}
