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

import verikc.base.ast.ComponentType
import verikc.base.ast.Line
import verikc.ps.ast.PsComponent
import verikc.sv.extract.SvIdentifierExtractorUtil
import verikc.sv.table.SvSymbolTable

data class SvComponent(
    val line: Line,
    val identifier: String,
    val componentType: ComponentType,
    val ports: List<SvPort>,
    val properties: List<SvProperty>,
    val componentInstances: List<SvComponentInstance>,
    val actionBlocks: List<SvActionBlock>,
    val methodBlocks: List<SvMethodBlock>
) {

    constructor(component: PsComponent, symbolTable: SvSymbolTable): this(
        component.line,
        SvIdentifierExtractorUtil.identifierWithoutUnderscore(component.identifier, component.line),
        component.componentType,
        component.ports.map { SvPort(it, symbolTable) },
        component.properties.map { SvProperty(it, symbolTable) },
        component.componentInstances.map { SvComponentInstance(it, symbolTable) },
        component.actionBlocks.map { SvActionBlock(it, symbolTable) },
        component.methodBlocks.map { SvMethodBlock(it, symbolTable) }
    )
}