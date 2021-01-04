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

package verikc.rf.reify

import verikc.rf.ast.RfEnum
import verikc.rf.ast.RfMethodBlock
import verikc.rf.ast.RfModule
import verikc.rf.symbol.RfSymbolTable

object RfReifierDeclaration: RfReifierBase() {

    override fun reifyModule(module: RfModule, symbolTable: RfSymbolTable) {
        module.methodBlocks.forEach { reifyMethodBlock(it, symbolTable) }
    }

    override fun reifyEnum(enum: RfEnum, symbolTable: RfSymbolTable) {
        symbolTable.addProperty(enum)
        symbolTable.addFunction(enum)
        enum.properties.forEach { symbolTable.addProperty(it) }
    }

    private fun reifyMethodBlock(methodBlock: RfMethodBlock, symbolTable: RfSymbolTable) {
        // TODO handle type parameters
        methodBlock.parameterProperties.forEach {
            it.typeReified = it.typeSymbol.toTypeReifiedInstance()
            symbolTable.addProperty(it)
        }
        methodBlock.returnTypeReified = methodBlock.returnTypeSymbol.toTypeReifiedInstance()
        symbolTable.addFunction(methodBlock)
    }
}