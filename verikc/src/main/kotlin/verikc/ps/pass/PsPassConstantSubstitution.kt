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

import verikc.base.ast.Line
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.TYPE_INT
import verikc.ps.ast.*

class PsPassConstantSubstitution: PsPassBase() {

    private val indexer = Indexer()

    override fun pass(compilationUnit: PsCompilationUnit) {
        indexer.pass(compilationUnit)
        super.pass(compilationUnit)
    }

    override fun passModule(module: PsModule) {
        module.actionBlocks.forEach { passBlock(it.block) }
        module.methodBlocks.forEach { passBlock(it.block) }
    }

    override fun passCls(cls: PsCls) {
        cls.methodBlocks.forEach { passBlock(it.block) }
    }

    private fun passBlock(block: PsBlock) {
        PsPassUtil.replaceBlock(block) { indexer.replace(it.expression) }
    }

    private class Indexer: PsPassBase() {

        private val expressionMap = HashMap<Symbol, PsExpression>()

        fun replace(expression: PsExpression): PsExpression? {
            return if (expression is PsExpressionProperty) {
                expressionMap[expression.propertySymbol]?.copy(expression.line)
            } else null
        }

        override fun passPrimaryProperty(primaryProperty: PsPrimaryProperty) {
            if (primaryProperty.expression is PsExpressionLiteral
                && primaryProperty.expression.typeGenerified.typeSymbol == TYPE_INT
            ) {
                expressionMap[primaryProperty.property.symbol] = primaryProperty.expression.copy(Line(0))
            }
        }
    }
}