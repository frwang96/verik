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
import verikc.ge.ast.GeModule
import verikc.ge.symbol.GeSymbolTable
import verikc.lang.LangSymbol.TYPE_UBIT

object GeGenerifierBulk: GeGenerifierBase() {

    override fun generifyModule(module: GeModule, symbolTable: GeSymbolTable) {
        module.actionBlocks.forEach {
            GeGenerifierBlock.generify(it.block, symbolTable)
            it.eventExpressions.forEach { expression ->
                GeGenerifierExpression.generify(expression, symbolTable)
            }
        }
        module.methodBlocks.forEach {
            GeGenerifierBlock.generify(it.block, symbolTable)
        }
    }

    override fun generifyEnum(enum: GeEnum, symbolTable: GeSymbolTable) {
        enum.properties.forEach {
            it.expression.typeGenerified = TYPE_UBIT.toTypeGenerifiedInstance(enum.width)
        }
    }
}
