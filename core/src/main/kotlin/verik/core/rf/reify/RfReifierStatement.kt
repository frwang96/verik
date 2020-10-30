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

package verik.core.rf.reify

import verik.core.base.ast.ReifiedType
import verik.core.base.ast.TypeClass.INSTANCE
import verik.core.lang.LangSymbol.TYPE_UINT
import verik.core.rf.ast.RfActionBlock
import verik.core.rf.ast.RfEnum
import verik.core.rf.ast.RfModule
import verik.core.rf.ast.RfStatementExpression
import verik.core.rf.symbol.RfSymbolTable

object RfReifierStatement: RfReifierBase() {

    override fun reifyModule(module: RfModule, symbolTable: RfSymbolTable) {
        module.actionBlocks.map { reifyDeclaration(it, symbolTable) }
    }

    override fun reifyEnum(enum: RfEnum, symbolTable: RfSymbolTable) {
        enum.entries.forEach {
            it.expression.reifiedType = ReifiedType(TYPE_UINT, INSTANCE, listOf(enum.width))
        }
    }

    override fun reifyActionBlock(actionBlock: RfActionBlock, symbolTable: RfSymbolTable) {
        actionBlock.block.statements.map {
            if (it is RfStatementExpression) {
                RfReifierExpression.reify(it.expression, symbolTable)
            }
        }
    }
}