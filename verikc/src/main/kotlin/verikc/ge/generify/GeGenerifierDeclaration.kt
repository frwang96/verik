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

package verikc.ge.generify

import verikc.ge.ast.GeEnum
import verikc.ge.ast.GeMethodBlock
import verikc.ge.ast.GeModule
import verikc.ge.symbol.GeSymbolTable

object GeGenerifierDeclaration: GeGenerifierBase() {

    override fun generifyModule(module: GeModule, symbolTable: GeSymbolTable) {
        module.methodBlocks.forEach { generifyMethodBlock(it, symbolTable) }
    }

    override fun generifyEnum(enum: GeEnum, symbolTable: GeSymbolTable) {
        symbolTable.addProperty(enum)
        symbolTable.addFunction(enum)
        enum.properties.forEach { symbolTable.addProperty(it) }
    }

    private fun generifyMethodBlock(methodBlock: GeMethodBlock, symbolTable: GeSymbolTable) {
        // TODO handle type parameters
        methodBlock.parameterProperties.forEach {
            it.typeGenerified = it.typeSymbol.toTypeGenerifiedInstance()
            symbolTable.addProperty(it)
        }
        methodBlock.returnTypeGenerified = methodBlock.returnTypeSymbol.toTypeGenerifiedInstance()
        symbolTable.addFunction(methodBlock)
    }
}