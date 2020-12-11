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

package verikc.sv.build

import verikc.sv.ast.SvControlBlockType
import verikc.sv.ast.SvExpression
import verikc.sv.ast.SvExpressionControlBlock
import verikc.sv.ast.SvStatementExpression

object SvCompoundExpressionBuilder {

    fun build(expression: SvExpression, builder: SvSourceBuilder) {
        if (expression is SvExpressionControlBlock) {
            buildControlBlock(expression, builder)
        } else {
            builder.appendln(SvSimpleExpressionBuilder.build(expression) + ";")
        }
    }

    private fun buildControlBlock(expression: SvExpressionControlBlock, builder: SvSourceBuilder) {
        when (expression.type) {
            SvControlBlockType.FOREVER -> {
                builder.append("forever ")
                expression.blocks[0].build(builder)
            }

            SvControlBlockType.IF -> {
                val condition = SvSimpleExpressionBuilder.build(expression.args[0])
                builder.append("if ($condition) ")
                expression.blocks[0].build(builder)
            }

            SvControlBlockType.IF_ELSE -> {
                val condition = SvSimpleExpressionBuilder.build(expression.args[0])
                builder.append("if ($condition) ")
                expression.blocks[0].build(builder)
                if (expression.blocks[1].statements.size == 1) {
                    val chainedStatement = expression.blocks[1].statements[0]
                    if (chainedStatement is SvStatementExpression
                        && chainedStatement.expression is SvExpressionControlBlock
                        && chainedStatement.expression.type in listOf(SvControlBlockType.IF, SvControlBlockType.IF_ELSE)
                    ) {
                        builder.append("else ")
                        chainedStatement.build(builder)
                    } else {
                        builder.append("else ")
                        expression.blocks[1].build(builder)
                    }
                } else {
                    builder.append("else ")
                    expression.blocks[1].build(builder)
                }
            }
        }
    }
}
