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

package verikc.rf.reify

import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.rf.ast.RfActionBlock
import verikc.rf.ast.RfEnum
import verikc.rf.ast.RfModule
import verikc.rf.ast.RfStatementExpression
import verikc.rf.symbol.RfSymbolTable

object RfReifierStatement: RfReifierBase() {

    override fun reifyModule(module: RfModule, symbolTable: RfSymbolTable) {
        module.actionBlocks.forEach { reifyActionBlock(it, symbolTable) }
    }

    override fun reifyEnum(enum: RfEnum, symbolTable: RfSymbolTable) {
        enum.properties.forEach {
            it.expression.typeReified = TYPE_UBIT.toTypeReifiedInstance(enum.width)
        }
    }

    override fun reifyActionBlock(actionBlock: RfActionBlock, symbolTable: RfSymbolTable) {
        actionBlock.block.statements.forEach {
            if (it is RfStatementExpression) {
                RfReifierExpression.reify(it.expression, symbolTable)
            }
        }
        actionBlock.eventExpressions.forEach {
            RfReifierExpression.reify(it, symbolTable)
        }
    }
}
