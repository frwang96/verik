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

package verikc.ps.pass

import verikc.base.ast.LineException
import verikc.base.ast.LiteralValue
import verikc.lang.LangSymbol.FUNCTION_NATIVE_LT_INT_INT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_POST_INCREMENT_INT
import verikc.lang.LangSymbol.FUNCTION_RANGE_INT
import verikc.lang.LangSymbol.OPERATOR_FOR
import verikc.lang.LangSymbol.OPERATOR_INTERNAL_FOR
import verikc.lang.LangSymbol.TYPE_BOOLEAN
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_UNIT
import verikc.ps.ast.*

object PsPassConvertLoop: PsPassBase() {

    override fun passComponent(component: PsComponent) {
        component.actionBlocks.forEach { passBlock(it.block) }
        component.methodBlocks.forEach { passBlock(it.block) }
    }

    override fun passCls(cls: PsCls) {
        passBlock(cls.instanceConstructor.block)
        cls.methodBlocks.forEach { passBlock(it.block) }
    }

    private fun passBlock(block: PsBlock) {
        PsPassUtil.replaceBlock(block) {
            if (!it.isSubexpression
                && it.expression is PsExpressionOperator
                && it.expression.operatorSymbol == OPERATOR_FOR
            ) {
                convertForLoop(it.expression)
            } else null
        }
    }

    private fun convertForLoop(expression: PsExpressionOperator): PsExpression {
        val iterableExpression = expression.args[0]
        val lambdaProperty = expression.blocks[0].lambdaProperties[0]
        return if (iterableExpression is PsExpressionFunction
            && iterableExpression.functionSymbol == FUNCTION_RANGE_INT
        ) {
            val forExpression = PsExpressionOperator(
                expression.line,
                TYPE_UNIT.toTypeGenerified(),
                OPERATOR_INTERNAL_FOR,
                null,
                arrayListOf(),
                expression.blocks
            )

            val propertyExpression = PsExpressionProperty(
                expression.line,
                lambdaProperty.typeGenerified,
                lambdaProperty.symbol,
                null
            )

            forExpression.args.add(
                PsExpressionLiteral(expression.line, TYPE_INT.toTypeGenerified(), LiteralValue.encodeInt(0))
            )
            forExpression.args.add(
                PsExpressionFunction(
                    expression.line,
                    TYPE_BOOLEAN.toTypeGenerified(),
                    FUNCTION_NATIVE_LT_INT_INT,
                    propertyExpression,
                    arrayListOf(iterableExpression.args[0])
                )
            )
            forExpression.args.add(
                PsExpressionFunction(
                    expression.line,
                    TYPE_UNIT.toTypeGenerified(),
                    FUNCTION_NATIVE_POST_INCREMENT_INT,
                    propertyExpression,
                    arrayListOf()
                )
            )

            forExpression
        } else throw LineException("unable to convert for loop", expression.line)
    }
}