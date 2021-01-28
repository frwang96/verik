/*
 * Copyright (c) 2021 Francis Wang
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

package verikc.rs.pass

import verikc.base.symbol.Symbol
import verikc.rs.ast.RsFunction
import verikc.rs.ast.RsType
import verikc.rs.table.RsSymbolTable

object RsPassBulk: RsPassBase() {

    override fun passType(type: RsType, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        if (type.instanceConstructor?.block != null) {
            RsPassBlock.pass(type.instanceConstructor.block, symbolTable)
        }
        type.functions.forEach { passFunction(it, type.symbol, symbolTable) }
    }

    override fun passFunction(function: RsFunction, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        RsPassBlock.pass(function.getBlockNotNull(), symbolTable)
    }
}
