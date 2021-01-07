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

package verikc.gex.generify

import verikc.gex.ast.GexFile
import verikc.gex.ast.GexFunction
import verikc.gex.ast.GexProperty
import verikc.gex.ast.GexType
import verikc.gex.table.GexSymbolTable

abstract class GexGenerifierBase {

    fun generifyFile(file: GexFile, symbolTable: GexSymbolTable) {
        file.types.forEach { generifyType(it, symbolTable) }
        file.functions.forEach { generifyFunction(it, symbolTable) }
        file.properties.forEach { generifyProperty(it, symbolTable) }
    }

    protected open fun generifyType(type: GexType, symbolTable: GexSymbolTable) {}

    protected open fun generifyFunction(function: GexFunction, symbolTable: GexSymbolTable) {}

    protected open fun generifyProperty(property: GexProperty, symbolTable: GexSymbolTable) {}
}
