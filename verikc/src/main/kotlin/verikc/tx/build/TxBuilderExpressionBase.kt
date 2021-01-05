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

package verikc.tx.build

import verikc.sv.ast.SvControlBlockType
import verikc.sv.ast.SvExpression
import verikc.sv.ast.SvExpressionControlBlock

object TxBuilderExpressionBase {

    fun build(expression: SvExpression, builder: TxSourceBuilder) {
        if (expression is SvExpressionControlBlock) {
            buildControlBlock(expression, builder)
        } else {
            builder.appendln(TxBuilderExpressionSimple.build(expression) + ";")
        }
    }

    private fun buildControlBlock(expression: SvExpressionControlBlock, builder: TxSourceBuilder) {
        when (expression.type) {
            SvControlBlockType.IF -> {
                val condition = TxBuilderExpressionSimple.build(expression.receiver!!)
                builder.append("if ($condition) ")
                TxBuilderBlock.buildBlock(expression.blocks[0], null, builder)
            }
            SvControlBlockType.IF_ELSE -> {
                val condition = TxBuilderExpressionSimple.build(expression.receiver!!)
                builder.append("if ($condition) ")
                TxBuilderBlock.buildBlock(expression.blocks[0], null, builder)
                builder.label(expression.blocks[1].line)
                if (expression.blocks[1].expressions.size == 1) {
                    val chainedExpression = expression.blocks[1].expressions[0]
                    if (chainedExpression is SvExpressionControlBlock
                        && chainedExpression.type in listOf(SvControlBlockType.IF, SvControlBlockType.IF_ELSE)
                    ) {
                        builder.append("else ")
                        build(chainedExpression, builder)
                    } else {
                        builder.append("else ")
                        TxBuilderBlock.buildBlock(expression.blocks[1], null, builder)
                    }
                } else {
                    builder.append("else ")
                    TxBuilderBlock.buildBlock(expression.blocks[1], null, builder)
                }
            }
            SvControlBlockType.FOREVER -> {
                builder.append("forever ")
                TxBuilderBlock.buildBlock(expression.blocks[0], null, builder)
            }
            SvControlBlockType.REPEAT -> {
                val repeatArg = TxBuilderExpressionSimple.build(expression.args[0])
                builder.append("repeat ($repeatArg) ")
                TxBuilderBlock.buildBlock(expression.blocks[0], null, builder)
            }
        }
    }
}
