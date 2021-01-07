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

package verikc.sv.extract

import verikc.ps.ast.PsComponentInstance
import verikc.sv.ast.SvComponentInstance
import verikc.sv.table.SvSymbolTable

object SvExtractorComponentInstance {

    fun extract(componentInstance: PsComponentInstance, symbolTable: SvSymbolTable): SvComponentInstance {
        return SvComponentInstance(
            componentInstance.property.line,
            componentInstance.property.identifier,
            symbolTable.extractTypeIdentifier(
                componentInstance.property.typeGenerified.typeSymbol,
                componentInstance.property.line
            ),
            componentInstance.connections.map { SvExtractorConnection.extract(it, symbolTable) }
        )
    }
}
