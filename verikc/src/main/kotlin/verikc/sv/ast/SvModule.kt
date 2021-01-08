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

package verikc.sv.ast

import verikc.base.ast.Line
import verikc.ps.ast.PsModule
import verikc.sv.extract.SvIdentifierExtractorUtil
import verikc.sv.table.SvSymbolTable

data class SvModule(
    override val line: Line,
    override val identifier: String,
    val ports: List<SvPort>,
    val properties: List<SvProperty>,
    val componentInstances: List<SvComponentInstance>,
    val actionBlocks: List<SvActionBlock>,
    val methodBlocks: List<SvMethodBlock>
): SvDeclaration {

    constructor(module: PsModule, symbolTable: SvSymbolTable): this(
        module.line,
        SvIdentifierExtractorUtil.identifierWithoutUnderscore(module),
        module.ports.map { SvPort(it, symbolTable) },
        module.properties.map { SvProperty(it, symbolTable) },
        module.componentInstances.map { SvComponentInstance(it, symbolTable) },
        module.actionBlocks.map { SvActionBlock(it, symbolTable) },
        module.methodBlocks.map { SvMethodBlock(it, symbolTable) }
    )
}